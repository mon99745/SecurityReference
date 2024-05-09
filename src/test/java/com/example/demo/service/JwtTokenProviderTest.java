package com.example.demo.service;

import com.example.demo.domain.Status;
import com.example.demo.domain.Token;
import com.example.demo.repository.TokenRepository;
import com.example.demo.util.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("토큰 발급 유틸 테스트")
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.MethodName.class)
@Transactional
@ActiveProfiles("test")
class JwtTokenProviderTest {
	@Autowired
	private TokenRepository tokenRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	private static String accessToken;
	private static String refreshToken;
	private static final String authority = "ROLE_USER";

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.access-token-valid-time}")
	private String accessTokenValidTime;

	@Value("${jwt.refresh-token-valid-time}")
	private String refreshTokenValidTime;

	@Test
	@DisplayName("토큰 생성 테스트")
	public void testGenerateToken() {
		// Arrange
		Authentication authentication = mock(Authentication.class);
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(authority));

		when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);

		// Act
		Token token = jwtTokenProvider.generateToken(authentication);
		accessToken = token.getAccessToken();
		refreshToken = token.getRefreshToken();

		// Assert
		assertNotNull(token);
		assertEquals("Bearer", token.getGrantType());
		assertNotNull(accessToken);
		assertNotNull(refreshToken);
	}

	@Test
	@DisplayName("유효 토큰을 통해 클레임 조회 테스트")
	public void testParseClaims_ValidToken() throws InterruptedException {
		// Arrange
		Claims claims = Jwts.claims().setSubject(null);
		claims.put("auth", authority);

		testGenerateToken();
		Thread.sleep(1000);

		// Act
		Claims ActClaims = jwtTokenProvider.parseClaims(accessToken);

		// Assert
		assertEquals(claims.getSubject(), ActClaims.getSubject());
		assertEquals(authority, claims.get("auth"));
	}

	@Test
	@DisplayName("무효 토큰을 통해 클레임 조회 테스트_예외")
	public void testParseClaims_InvalidToken() throws InterruptedException {
		// Arrange
		Claims claims = Jwts.claims().setSubject(null);
		claims.put("auth", authority);

		testGenerateToken();
		Thread.sleep(1000);


		Token.ValidToken token = tokenRepository.findByAccessToken(accessToken).get();
		token.setStatus(Status.INVALID);
		tokenRepository.save(token);

		Thread.sleep(1000);

		// Act & Assert
		assertThrows(RuntimeException.class, () -> {
			jwtTokenProvider.parseClaims(accessToken);
		});
	}

	@Test
	@DisplayName("유효 토큰 검증 테스트")
	public void testValidateToken_ValidToken() throws InterruptedException {
		// Arrange
		testGenerateToken();
		Thread.sleep(1000);

		// Act
		boolean accessTokenIsValid = jwtTokenProvider.validateToken(accessToken);
		boolean refreshTokenIsValid = jwtTokenProvider.validateToken(refreshToken);

		// Assert
		assertTrue(accessTokenIsValid);
		assertTrue(refreshTokenIsValid);
	}

	@Test
	@DisplayName("무효 토큰 검증 테스트_예외")
	public void testValidateToken_InvalidToken() throws InterruptedException {
		// Arrange
		testGenerateToken();
		Thread.sleep(1000);

		Token.ValidToken token = tokenRepository.findByRefreshToken(refreshToken).get().builder()
				.status(Status.INVALID)
				.build();

		tokenRepository.save(token);

		// Act
		boolean refreshTokenIsValid = jwtTokenProvider.validateToken(token.getRefreshToken());

		// Assert
		assertFalse(refreshTokenIsValid);
	}
}