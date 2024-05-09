package com.example.demo.service;

import com.example.demo.domain.Status;
import com.example.demo.domain.Token;
import com.example.demo.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;


	/**
	 * getToken in header
	 *
	 * @param request
	 * @return
	 */
	public Token getToken(HttpServletRequest request) {
		String accessToken = request.getHeader("Authorization");
		if (accessToken != null && accessToken.startsWith("Bearer ")) {
			accessToken = accessToken.substring(7);
		}

		String refreshToken = request.getHeader("X-Refresh-Token");
		if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
			refreshToken = refreshToken.substring(7);
		}

		return Token.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

    /**
     * 토큰 상태 변경
     *
     * @param token
     * @param status
     * @return
     */
    public boolean updateStatusToken(Token token, Status status) {
        try {
			tokenRepository.save(Token.ValidToken.builder()
					.accessToken(token.getAccessToken())
					.refreshToken(token.getRefreshToken())
					.status(status)
					.build());
		} catch (RuntimeException e) {
			log.info("토큰 상태 변경 중 문제가 발생하였습니다. "+ e);
		}
		return true;
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
