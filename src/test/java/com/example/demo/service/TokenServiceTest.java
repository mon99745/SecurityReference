package com.example.demo.service;

import com.example.demo.config.annotation.ServiceTest;
import com.example.demo.domain.Status;
import com.example.demo.domain.Token;
import com.example.demo.repository.TokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ServiceTest
@DisplayName("토큰 서비스 테스트")
class TokenServiceTest {
	@Mock
	private HttpServletRequest request;
	@Mock
	private TokenRepository tokenRepository;
	@InjectMocks
	private TokenService tokenService;

	@Test
	@DisplayName("헤더에서 토큰 정보 추출 테스트 [Access Token]")
	public void testGetToken_AuthorizationHeader() {
		// Arrange
		String testAccessToken = "Bearer testAccessToken";

		when(request.getHeader("Authorization")).thenReturn(testAccessToken);

		// Act
		Token token = tokenService.getToken(request);

		// Assert
		assertEquals("testAccessToken", token.getAccessToken());
	}

	@Test
	@DisplayName("헤더에서 토큰 정보 추출 테스트 [Refresh Token]")
	public void testGetToken_RefreshTokenHeader() {
		// Arrange
		String testRefreshToken = "Bearer testRefreshToken";

		when(request.getHeader("X-Refresh-Token")).thenReturn(testRefreshToken);

		// Act
		Token token = tokenService.getToken(request);

		// Assert
		assertEquals("testRefreshToken", token.getRefreshToken());
	}

	@Test
	@DisplayName("헤더에서 토큰 정보 추출 테스트 [Tokens]")
	public void testGetToken_BothHeaders() {
		// Arrange
		String testAccessToken = "Bearer testAccessToken";
		String testRefreshToken = "Bearer testRefreshToken";

		when(request.getHeader("Authorization")).thenReturn(testAccessToken);
		when(request.getHeader("X-Refresh-Token")).thenReturn(testRefreshToken);

		// Act
		Token token = tokenService.getToken(request);

		// Assert
		assertEquals("testAccessToken", token.getAccessToken());
		assertEquals("testRefreshToken", token.getRefreshToken());
	}

	@Test
	@DisplayName("토큰 상태 변경 테스트")
	public void testUpdateStatusToken() {
		// Arrange
		Token token = Token.builder()
				.accessToken("ValidAccessToken")
				.refreshToken("ValidRefreshToken")
				.build();

		Status status = Status.INVALID;

		// Act
		tokenService.updateStatusToken(token, status);

		// Assert
		verify(tokenRepository).save(any(Token.ValidToken.class));
	}
}