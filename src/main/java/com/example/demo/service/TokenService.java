package com.example.demo.service;

import com.example.demo.domain.Status;
import com.example.demo.domain.Token;
import com.example.demo.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

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
				.accessToken(accessToken)
				.status(status)
				.build());
	}
}
