package com.example.demo.service;

import com.example.demo.domain.Status;
import com.example.demo.domain.Token;
import com.example.demo.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {
	private final TokenRepository tokenRepository;

	/**
	 * getAccessToken in header
	 *
	 * @param request
	 * @return
	 */
	public String getAccessToken(HttpServletRequest request) {
		String accessToken = request.getHeader("Authorization");
		if (accessToken != null && accessToken.startsWith("Bearer ")) {
			accessToken = accessToken.substring(7);
		}
		return accessToken;
	}

	/**
	 * 토큰 상태 변경
	 *
	 * @param accessToken
	 * @param status
	 * @return
	 */
	public void updateStatusToken(String accessToken, Status status) {
		tokenRepository.save(Token.ValidToken.builder()
				//TODO 로그아웃 시, 리프레쉬 토큰을 삭제할 것인지
				.accessToken(accessToken)
				.status(status)
				.build());
	}

	/**
	 * 저장소에 refreshToken 존재 여부 확인
	 *
	 * @param refreshToken
	 * @return
	 */
	public boolean existsRefreshToken(String refreshToken) {
		Optional<Token.ValidToken> rToken = tokenRepository.findByRefreshToken(refreshToken);
		return rToken.isPresent();
	}
}
