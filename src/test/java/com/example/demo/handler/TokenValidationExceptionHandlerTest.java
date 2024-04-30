package com.example.demo.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TokenValidationExceptionHandlerTest {

	@Mock
	private RedirectAttributes redirectAttributes;

	@InjectMocks
	private TokenValidationExceptionHandler exceptionHandler;

	@Test
	public void testHandleTokenValidationException_InvalidToken() {
		// Arrange
		io.jsonwebtoken.security.SecurityException exception = new io.jsonwebtoken
				.security.SecurityException("Invalid JWT Token");

		// Act
		String redirectUrl = exceptionHandler.handleTokenValidationException(exception, redirectAttributes);

		// Assert
		verify(redirectAttributes).addFlashAttribute("error", "invalid-token");
		assert redirectUrl.equals("redirect:/login-page");
	}

	@Test
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
	public void testHandleTokenValidationException_UnsupportedToken() {
		// Arrange
		UnsupportedJwtException exception = new UnsupportedJwtException("Unsupported JWT Token");

		// Act
		String redirectUrl = exceptionHandler.handleTokenValidationException(exception, redirectAttributes);

		// Assert
		verify(redirectAttributes).addFlashAttribute("error", "unsupported-token");
		assert redirectUrl.equals("redirect:/login-page");
	}

	@Test
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