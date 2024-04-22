package com.example.demo.service;

import com.example.demo.domain.Status;
import com.example.demo.domain.TokenInfo;
import com.example.demo.domain.ValidToken;
import com.example.demo.repository.ValidTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
	private final ValidTokenRepository validTokenRepository;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtTokenProvider jwtTokenProvider;

	@Transactional
	public TokenInfo login(String username, String password) {
		// 1. Login ID/PW 를 기반으로 Authentication 객체 생성
		// 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

		// 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
		// authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		// 3. 인증 정보를 기반으로 JWT 토큰 생성
		TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

		return tokenInfo;
	}

	@Transactional
	public void logout(HttpServletRequest request) {
		// 엑세스 토큰 추출
		String accessToken = request.getHeader("Authorization");
		if (accessToken != null && accessToken.startsWith("Bearer ")) {
			accessToken = accessToken.substring(7);
		}
		// 토큰 목록에서 상태를 변경
		validTokenRepository.save(ValidToken.builder()
				.accessToken(accessToken)
				.status(Status.REVOKED)
				.build());
	}
}