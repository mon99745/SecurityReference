package com.example.demo.service;

import com.example.demo.domain.Status;
import com.example.demo.domain.Token;
import com.example.demo.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * User - Main elements of service
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
	private final TokenRepository tokenRepository;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtTokenProvider jwtTokenProvider;
	@Transactional
	public Token login(String username, String password) {
		// 1. Login ID/PW 를 기반으로 Authentication 객체 생성
		// 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

		// 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
		// authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		// 3. 인증 정보를 기반으로 JWT 토큰 생성
		Token token = jwtTokenProvider.generateToken(authentication);

		return token;
	}

	@Transactional
	public void logout(HttpServletRequest request) {
		// 엑세스 토큰 추출
		String accessToken = request.getHeader("Authorization");
		if (accessToken != null && accessToken.startsWith("Bearer ")) {
			accessToken = accessToken.substring(7);
		}
		// 토큰 목록에서 상태를 변경
		tokenRepository.save(Token.ValidToken.builder()
				.accessToken(accessToken)
				.status(Status.REVOKED)
				.build());
	}
}