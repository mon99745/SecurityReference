package com.example.demo.filter;

import com.example.demo.domain.Token;
import com.example.demo.service.TokenService;
import com.example.demo.util.JwtTokenProvider;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Token - Filter
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

	private final JwtTokenProvider jwtTokenProvider;
	private final TokenService tokenService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException, java.io.IOException {

		// 1. Request Header 에서 JWT 토큰 추출
		Token token = resolveToken((HttpServletRequest) request);
		String accessToken = token.getAccessToken();
		String refreshToken = token.getRefreshToken();

		// 2. Token 유효성 검사
		if (accessToken != null) {
			// 2-1 Access Token 검사
			if (jwtTokenProvider.validateToken(accessToken)) {
				setAuthenticationInContext(accessToken);
			} else if (refreshToken != null && tokenService.existsRefreshToken(refreshToken)) {
				if (jwtTokenProvider.validateToken(refreshToken));
				// Access Token 재발급
				Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
				String newAccessToken = jwtTokenProvider.regenerateToken(authentication, token.getRefreshToken())
						.getAccessToken();

				// Header 에 Access Token 추가해서 반환
				jwtTokenProvider.setHeaderAccessToken((HttpServletResponse) response, newAccessToken);

				// SecurityContext 에 저장
				this.setAuthenticationInContext(newAccessToken);
			} else {
				throw new RuntimeException("토큰 정보가 정확하지 않습니다 Access Token :" + accessToken
						+ "Refresh Token: " + refreshToken );
			}
		}
		chain.doFilter(request, response);
	}

	private void setAuthenticationInContext(String accessToken) {
		//토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
		Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
		SecurityContextHolder
				.getContext()
				.setAuthentication(authentication);
	}
	// Request Header 에서 토큰 정보 추출
	private Token resolveToken(HttpServletRequest request) {
		String accessToken = null;
		String refreshToken = null;

		String accessBearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(accessBearerToken) && accessBearerToken.startsWith("Bearer")) {
			accessToken = accessBearerToken.substring(7);
		}

		String refreshBearerToken = request.getHeader("X-Refresh-Token");
		if (StringUtils.hasText(refreshBearerToken) && refreshBearerToken.startsWith("Bearer")) {
			refreshToken = refreshBearerToken.substring(7);
		}
		Token token = Token.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();

		return token;
	}
}