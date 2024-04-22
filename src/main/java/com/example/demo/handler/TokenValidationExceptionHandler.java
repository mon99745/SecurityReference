package com.example.demo.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Token - Exception handler for validation
 */
@ControllerAdvice
public class TokenValidationExceptionHandler {

	@ExceptionHandler(value = { io.jsonwebtoken.security.SecurityException.class, MalformedJwtException.class,
			ExpiredJwtException.class, UnsupportedJwtException.class,
			IllegalArgumentException.class })
	public String handleTokenValidationException(Exception ex, RedirectAttributes redirectAttributes) {
		if (ex instanceof io.jsonwebtoken.security.SecurityException || ex instanceof MalformedJwtException) {
			redirectAttributes.addFlashAttribute("error", "invalid-token");
		} else if (ex instanceof ExpiredJwtException) {
			redirectAttributes.addFlashAttribute("error", "expired-token");
		} else if (ex instanceof UnsupportedJwtException) {
			redirectAttributes.addFlashAttribute("error", "unsupported-token");
		} else if (ex instanceof IllegalArgumentException) {
			redirectAttributes.addFlashAttribute("error", "empty-claim");
		} else {
			redirectAttributes.addFlashAttribute("error", "unknown-error");
		}

		return "redirect:/login-page";
	}
}