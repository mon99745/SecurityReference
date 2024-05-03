package com.example.demo.service;

import com.example.demo.domain.Status;
import com.example.demo.domain.Token;
import com.example.demo.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

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

	/**
	 * refreshToken 유효성 검증
	 * TODO: 문제지점
	 * @param refreshToken
	 * @return
	 */
	public boolean validRefreshToken(String refreshToken) {
		Token.ValidToken token = tokenRepository.findByRefreshToken(refreshToken);

		Date now = new Date(System.currentTimeMillis());
		if (!token.getStatus().equals(Status.VALID) || token.getExpireDate().before(now)) {
			throw new RuntimeException("사용할 수 없는 RefreshToken 입니다. RefreshToken: " + token.getRefreshToken());
		}
		return !refreshToken.equals(token.getRefreshToken());
	}


}
