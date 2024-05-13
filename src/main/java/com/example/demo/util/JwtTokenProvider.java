package com.example.demo.util;

import com.example.demo.domain.Status;
import com.example.demo.domain.Token;
import com.example.demo.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Token - JWT Provider
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
	private final TokenRepository tokenRepository;

	@Value("${jwt.access-token-valid-time}")
	public String accessTokenValidTime;

	@Value("${jwt.refresh-token-valid-time}")
	private String refreshTokenValidTime;

	@Value("${jwt.secret}")
	private String secretKey;

	/**
	 * User 정보를 통해 AccessToken, RefreshToken 생성
	 *
	 * @param authentication
	 * @return
	 */
	public Token generateToken(Authentication authentication) {
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
				.signWith(encrypt(secretKey), SignatureAlgorithm.HS256)
				.compact();

		// Refresh Token 생성
		Date refreshTokenExpiresIn = new Date(now + Integer.parseInt(refreshTokenValidTime));
		String refreshToken = Jwts.builder()
				.setSubject(authentication.getName())
				.claim("auth", authorities)
				.setExpiration(refreshTokenExpiresIn)
				.signWith(encrypt(secretKey), SignatureAlgorithm.HS256)
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

	/**
	 * RefreshToken을 통해 AccessToken을 재발급
	 *
	 * @param authentication
	 * @param refreshToken
	 * @return
	 */
	public Token regenerateToken(Authentication authentication, String refreshToken) {
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
				.signWith(encrypt(secretKey), SignatureAlgorithm.HS256)
				.compact();

		// 토큰 정보에 Token 저장 및 상태 정보 저장
		Optional<Token.ValidToken> token = tokenRepository.findByRefreshToken(refreshToken);
		if (token.isPresent()) {
			token.get().setAccessToken(accessToken);
			tokenRepository.save(token.get());
		}

		return Token.builder()
				.grantType("Bearer")
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	/**
	 * JWT 토큰을 복호화하여 토큰의 권한 정보를 추출
	 *
	 * @param accessToken, refreshToken
	 * @return
	 */
	public Authentication getAuthentication(String accessToken, String refreshToken) {
		// 토큰 복호화
		Claims claims = parseClaims(accessToken, refreshToken);

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

	/**
	 * JWT 토큰 정보를 검증하는 메서드
	 *
	 * @param token
	 * @return
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(encrypt(secretKey))
					.build().parseClaimsJws(token);
			Token.ValidToken tokenObject = tokenRepository.findByAccessToken(token)
					.orElseGet(() -> tokenRepository.findByRefreshToken(token)
							.orElseThrow(() -> new RuntimeException("Token not found")));
			if (!tokenObject.getStatus().equals(Status.VALID)) {
				throw new RuntimeException("사용 불가능한 토큰입니다. Token = " + token);
			}
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			log.info("Invalid JWT Token", e);
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT Token", e);
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT Token", e);
		} catch (IllegalArgumentException e) {
			log.info("JWT claims string is empty.", e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	/**
	 * Token Claim 추출
	 *
	 * @param accessToken, refreshToken
	 * @return
	 */
	public Claims parseClaims(String accessToken, String refreshToken) {
		Token.ValidToken token = null;
		String targetToken = null;

		try {
			if (accessToken != null) {
				targetToken = accessToken;
				token = tokenRepository.findByAccessToken(targetToken)
						.orElseThrow(() -> new RuntimeException("Access Token not found"));
			} else if (refreshToken != null) {
				targetToken = refreshToken;
				token = tokenRepository.findByRefreshToken(targetToken)
						.orElseThrow(() -> new RuntimeException("Refresh Token not found"));
			} else {
				new RuntimeException("All Token is Empty");
			}

			if (!token.getStatus().equals(Status.VALID)) {
				throw new RuntimeException("사용 불가능한 토큰입니다. Token = " + token);
			}
			return Jwts.parserBuilder()
					.setSigningKey(encrypt(secretKey)).build()
					.parseClaimsJws(targetToken).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	/**
	 * Key 암호화
	 *
	 * @param secretKey
	 * @return
	 */
	public Key encrypt(String secretKey) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * AccessToken Format 설정
	 *
	 * @param response
	 * @param accessToken
	 */
	public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
		response.setHeader("authorization", "bearer " + accessToken);
	}

	/**
	 * RefreshToken Format 설정
	 *
	 * @param response
	 * @param refreshToken
	 */
	public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
		response.setHeader("refreshToken", "bearer " + refreshToken);
	}
}