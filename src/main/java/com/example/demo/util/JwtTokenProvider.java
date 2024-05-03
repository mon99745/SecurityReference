package com.example.demo.util;

import com.example.demo.domain.Status;
import com.example.demo.domain.Token;
import com.example.demo.repository.TokenRepository;
import com.example.demo.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Token - JWT Provider
 */
@Slf4j
@Component
public class JwtTokenProvider {
	private final Key key;
	private final TokenService tokenService;
	private final TokenRepository tokenRepository;

	@Value("${jwt.access-token-valid-time}")
	private String accessTokenValidTime;

	@Value("${jwt.refresh-token-valid-time}")
	private String refreshTokenValidTime;

	public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
							TokenService tokenService, TokenRepository tokenRepository) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
		this.tokenService = tokenService;
		this.tokenRepository = tokenRepository;
	}

	// 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
	public Token generateToken(Authentication authentication) {
		if (accessTokenValidTime.isEmpty() || refreshTokenValidTime.isEmpty()) {
			throw new RuntimeException("토큰의 유효시간 설정이 잘못되었습니다. " +
					"accessTokenExpTime: " + accessTokenValidTime + "refreshTokenExpTime: " + refreshTokenValidTime);
		}
		// 권한 가져오기
		String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		long now = (new Date()).getTime();
		// Access Token 생성
		Date accessTokenExpiresIn = new Date(now + Integer.parseInt(accessTokenValidTime));
		String accessToken = Jwts.builder()
				.setSubject(authentication.getName())
				.claim("auth", authorities)
				.setExpiration(accessTokenExpiresIn)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();

		// Refresh Token 생성
		Date refreshTokenExpiresIn = new Date(now + Integer.parseInt(refreshTokenValidTime));
		String refreshToken = Jwts.builder()
				.setExpiration(refreshTokenExpiresIn)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();


		// 토큰 정보에 Token 저장 및 상태 정보 저장
		tokenRepository.save(Token.ValidToken.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.expireDate(refreshTokenExpiresIn)
				.status(Status.VALID)
				.build());

		return Token.builder()
				.grantType("Bearer")
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	public Token generateToken(Authentication authentication, String refreshToken) {
		if (accessTokenValidTime.isEmpty() || refreshTokenValidTime.isEmpty()) {
			throw new RuntimeException("토큰의 유효시간 설정이 잘못되었습니다. " +
					"accessTokenExpTime: " + accessTokenValidTime + "refreshTokenExpTime: " + refreshTokenValidTime);
		}
		// 권한 가져오기
		String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		// Access Token 생성
		long now = (new Date()).getTime();
		Date accessTokenExpiresIn = new Date(now + Integer.parseInt(accessTokenValidTime));
		String accessToken = Jwts.builder()
				.setSubject(authentication.getName())
				.claim("auth", authorities)
				.setExpiration(accessTokenExpiresIn)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();

		// 토큰 정보에 Token 저장 및 상태 정보 저장
		tokenRepository.save(Token.ValidToken.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.status(Status.VALID)
				.build());

		return Token.builder()
				.grantType("Bearer")
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	// JWT 토큰을 복호화하여 토큰의 권한 정보 추출 메서드
	public Authentication getAuthentication(String accessToken) {
		// 토큰 복호화
		Claims claims = parseClaims(accessToken);

		if (claims.get("auth") == null) {
			throw new RuntimeException("권한 정보가 없는 토큰입니다.");
		}

		// 클레임에서 권한 정보 가져오기
		Collection<? extends GrantedAuthority> authorities =
				Arrays.stream(claims.get("auth").toString().split(","))
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList());

		// UserDetails 객체를 만들어서 Authentication 리턴
		UserDetails principal = new User(claims.getSubject(), "", authorities);
		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	// 토큰 정보를 검증하는 메서드
	public boolean validateToken(Token token) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(key)
					.build().parseClaimsJws(token.getAccessToken());

//			TODO: access 토큰의 상태가 무효나 폐기일때는?
//			Token.ValidToken validToken = tokenRepository.findByAccessToken(token);
//			if (!validToken.getStatus().equals(Status.VALID)) {
//				throw new Exception("사용 불가능한 토큰입니다. Token = " + token);
//			}
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			log.info("Invalid JWT Token", e);
		} catch (ExpiredJwtException accessTokenEx) {
			try {
				log.info(" accessToken are expired.", accessTokenEx);

				//TODO
				//Requset Header 에 있는 RefreshToken과 DB 에 있는 RefreshToken을 비교해서 있으면 AccessToken 을 재발급
				if (tokenService.validRefreshToken(token.getRefreshToken())) {
					Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
					generateToken(authentication, token.getRefreshToken());
				}
			} catch (ExpiredJwtException refreshTokenEx) {
				// accessToken, refreshToken 모두 만료된 경우
				log.info("Both accessToken and refreshToken are expired.", refreshTokenEx);
			} catch (JwtException e) {
				log.error("Invalid refreshToken.", e);
			}
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT Token", e);
		} catch (IllegalArgumentException e) {
			log.info("JWT claims string is empty.", e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	public Claims parseClaims(String accessToken) {
		try {
			return Jwts.parserBuilder()
					.setSigningKey(key).build()
					.parseClaimsJws(accessToken).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}
}