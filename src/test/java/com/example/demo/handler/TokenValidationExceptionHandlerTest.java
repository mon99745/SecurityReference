package com.example.demo.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.mockito.Mockito.verify;

@DisplayName("토큰 검증 예외 핸들러 테스트")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TokenValidationExceptionHandlerTest {
	@Mock
	private RedirectAttributes redirectAttributes;

	@InjectMocks
	private TokenValidationExceptionHandler exceptionHandler;

	@Test
	@DisplayName("유효하지 않은 토큰의 경우_예외")
	public void testHandleTokenValidationException_InvalidToken() {
		// Arrange
		// SecurityException: 서명이나 토큰 정보가 유효하지 않을 경우 발생
		io.jsonwebtoken.security.SecurityException exception = new io.jsonwebtoken
				.security.SecurityException("Invalid JWT Token");

		// Act
		String redirectUrl = exceptionHandler.handleTokenValidationException(exception, redirectAttributes);

		// Assert
		verify(redirectAttributes).addFlashAttribute("error", "invalid-token");
		assert redirectUrl.equals("redirect:/login-page");
	}

	@Test
	@DisplayName("기간 만료된 토큰의 경우_예외")
	public void testHandleTokenValidationException_ExpiredToken() {
		// Arrange
		ExpiredJwtException exception = new ExpiredJwtException(null, null, "Expired JWT Token");

		// Act
		String redirectUrl = exceptionHandler.handleTokenValidationException(exception, redirectAttributes);

		// Assert
		verify(redirectAttributes).addFlashAttribute("error", "expired-token");
		assert redirectUrl.equals("redirect:/login-page");
	}

	@Test
	@DisplayName("지원하지 않는 토큰의 경우_예외")
	public void testHandleTokenValidationException_UnsupportedToken() {
		// Arrange
		// UnsupportedJwtException: JWT의 형식이나 내용이 지원되지 않을 때 발생
		UnsupportedJwtException exception = new UnsupportedJwtException("Unsupported JWT Token");

		// Act
		String redirectUrl = exceptionHandler.handleTokenValidationException(exception, redirectAttributes);

		// Assert
		verify(redirectAttributes).addFlashAttribute("error", "unsupported-token");
		assert redirectUrl.equals("redirect:/login-page");
	}

	@Test
	@DisplayName("토큰의 클레임이 비어있는 경우_예외")
	public void testHandleTokenValidationException_EmptyClaim() {
		// Arrange
		IllegalArgumentException exception = new IllegalArgumentException("JWT claims string is empty.");

		// Act
		String redirectUrl = exceptionHandler.handleTokenValidationException(exception, redirectAttributes);

		// Assert
		verify(redirectAttributes).addFlashAttribute("error", "empty-claim");
		assert redirectUrl.equals("redirect:/login-page");
	}

	@Test
	@DisplayName("이외의 토큰 문제가 발생할 경우_예외")
	public void testHandleTokenValidationException_UnknownError() {
		// Arrange
		RuntimeException exception = new RuntimeException("Unknown error occurred.");

		// Act
		String redirectUrl = exceptionHandler.handleTokenValidationException(exception, redirectAttributes);

		// Assert
		verify(redirectAttributes).addFlashAttribute("error", "unknown-error");
		assert redirectUrl.equals("redirect:/login-page");
	}
}