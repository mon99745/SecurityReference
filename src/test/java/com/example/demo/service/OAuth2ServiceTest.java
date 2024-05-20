package com.example.demo.service;

import com.example.demo.config.annotation.ServiceTest;
import com.example.demo.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.servlet.http.HttpSession;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;


@ServiceTest
@DisplayName("OAuth2.0 서비스 테스트")
class OAuth2ServiceTest {
	@Mock
	private OAuth2UserService<OAuth2UserRequest, OAuth2User> defaultOAuth2UserService;

	@Mock
	private UserService userService;

	@Mock
	private HttpSession httpSession;

	@InjectMocks
	private OAuth2Service oAuth2Service;

	private OAuth2UserRequest oAuth2UserRequest;
	private OAuth2User oAuth2User;
	private ClientRegistration clientRegistration;
	private OAuth2AccessToken oAuth2AccessToken;

	/**
	 * 사용 가능한 Client 정보를 기입할 것
	 */
	@BeforeEach
	public void setUp() {
		clientRegistration = ClientRegistration.withRegistrationId("test")
				.clientId("clientId")
				.clientSecret("clientSecret")
				.authorizationGrantType(AUTHORIZATION_CODE)
				.authorizationUri("http://auth-uri")
				.tokenUri("http://token-uri")
				.redirectUriTemplate("http://redirect-uri")
				.userInfoUri("http://userinfo-uri")
				.userNameAttributeName("id")
				.clientName("test")
				.build();

		oAuth2AccessToken = new OAuth2AccessToken(
				OAuth2AccessToken.TokenType.BEARER,
				"mock-token",
				Instant.now(),
				Instant.now().plusSeconds(3600)
		);

		oAuth2UserRequest = new OAuth2UserRequest(clientRegistration, oAuth2AccessToken);

		Map<String, Object> attributes = new HashMap<>();
		attributes.put("id", "12345");
		attributes.put("name", "testUser");

		oAuth2User = new DefaultOAuth2User(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
				attributes, "id");
	}

//	@Test
	@DisplayName(" OAuth2 인증 절차 테스트")
	public void testLoadUser() {
		// Arrange
		when(defaultOAuth2UserService.loadUser(oAuth2UserRequest)).thenReturn(oAuth2User);

		// Act
		OAuth2User result = oAuth2Service.loadUser(oAuth2UserRequest);

		// Assert
		verify(userService, times(1)).create(any(User.class));
		verify(httpSession, times(1)).setAttribute(eq("username"), eq("testUser"));
		verify(httpSession, times(1)).setAttribute(eq("tmpPassword"), anyString());

		assertEquals(oAuth2User, result);
	}

//	@Test
	@DisplayName(" OAuth2 인증 절차 테스트_예외")
	public void testLoadUserThrowsException() {
		// Arrange
		when(defaultOAuth2UserService.loadUser(oAuth2UserRequest)).thenThrow(new OAuth2AuthenticationException("Error"));

		// Act
		assertThrows(OAuth2AuthenticationException.class, () -> {
			oAuth2Service.loadUser(oAuth2UserRequest);
		});

		// Assert
		verify(userService, never()).create(any(User.class));
		verify(httpSession, never()).setAttribute(eq("username"), anyString());
		verify(httpSession, never()).setAttribute(eq("tmpPassword"), anyString());
	}
}