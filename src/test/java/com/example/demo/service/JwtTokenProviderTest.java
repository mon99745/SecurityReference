//package com.example.demo.service;
//
//import com.example.demo.domain.Status;
//import com.example.demo.domain.Token;
//import com.example.demo.repository.TokenRepository;
//import com.example.demo.util.JwtTokenProvider;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtParser;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.ArrayList;
//import java.util.Collection;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@TestMethodOrder(MethodOrderer.MethodName.class)
//@ActiveProfiles("test")
//class JwtTokenProviderTest {
//
//	@Mock
//	private TokenService tokenService;
//	@Mock
//	private TokenRepository tokenRepository;
//	private JwtTokenProvider jwtTokenProvider;
//	private static String accessToken;
//	@Value("${jwt.secret}")
//	private String secretKey;
//
//	@Value("${jwt.access-token-valid-time}")
//	private String accessTokenValidTime;
//
//	@Value("${jwt.refresh-token-valid-time}")
//	private String refreshTokenValidTime;
//
//	@BeforeEach
//	public void setUp() {
//		jwtTokenProvider = new JwtTokenProvider(secretKey, tokenService, tokenRepository);
//	}
//
//	@Test // 토큰을 생성
//	public void t01GenerateToken() {
//		// 준비
//		String username = "testUser";
//		String authority = "ROLE_USER";
//
//		// 준비
//		Authentication authentication = mock(Authentication.class);
//		Collection<GrantedAuthority> authorities = new ArrayList<>();
//		authorities.add(new SimpleGrantedAuthority(authority));
//		when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
//
//		// 실행
//		Token token = jwtTokenProvider.generateToken(authentication);
//		accessToken = token.getAccessToken();
//
//		// 검증
//		assertNotNull(token);
//		assertEquals("Bearer", token.getGrantType());
//		assertNotNull(token.getAccessToken());
//		assertNotNull(token.getRefreshToken());
//		verify(tokenRepository).save(any(Token.ValidToken.class));
//	}
//
//	//	@Test // 토큰의 클레임 검증
//	public void t02GetAuthentication_ValidToken() {
//		// 준비
////		String accessToken = "validAccessToken";
//
//		Claims claims = Jwts.claims().setSubject("testUser");
//		claims.put("auth", "ROLE_USER");
//		JwtParser jwtParser = mock(JwtParser.class);
//
//		String token = Jwts.builder().
//				setClaims(claims).
//				signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256))
//				.compact();
//
//		when(jwtTokenProvider.parseClaims(token))
//				.thenReturn(claims);
//		when(tokenRepository.findByAccessToken(accessToken))
//				.thenReturn(Token.ValidToken.builder()
//						.status(Status.VALID)
//						.build());
//
//		// 실행
//		Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
//
//		// 검증
//		assertNotNull(authentication);
//		assertEquals("testUser", authentication.getName());
//		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//		assertNotNull(authorities);
//		assertEquals(1, authorities.size());
//		assertEquals("ROLE_USER", authorities.iterator().next().getAuthority());
//	}
//
//	//	@Test
//	public void testGetAuthentication_InvalidToken() {
//		// 준비
//		String accessToken = "invalidTokenString";
//		when(jwtTokenProvider.parseClaims(accessToken))
//				.thenThrow(MalformedJwtException.class);
//		when(tokenRepository.findByAccessToken(accessToken))
//				.thenReturn(Token.ValidToken.builder()
//						.status(Status.INVALID)
//						.build());
//
//		// 실행 및 검증
//		assertThrows(RuntimeException.class, () -> {
//			jwtTokenProvider.getAuthentication(accessToken);
//		});
//	}
//
//	//	@Test
//	public void testValidateToken_ValidToken() {
//		// 준비
//		Token token = Token.builder()
//				.accessToken("ValidAccessToken")
//				.refreshToken("ValidRefreshToken")
//				.build();
//
//		when(tokenRepository.findByAccessToken(accessToken))
//				.thenReturn(Token.ValidToken.builder()
//						.status(Status.VALID)
//						.build());
//
//		// 실행
//		boolean isValid = jwtTokenProvider.validateToken(token);
//
//		// 검증
//		assertTrue(isValid);
//	}
//
//	@Test
//	public void testValidateToken_InvalidToken() {
//		// 준비
//		Token token = Token.builder()
//				.accessToken("invalidAccessToken")
//				.refreshToken("invalidRefreshToken")
//				.build();
//
//		when(tokenRepository.findByAccessToken(accessToken))
//				.thenReturn(Token.ValidToken.builder()
//						.status(Status.INVALID)
//						.build());
//
//		// 실행
//		boolean isValid = jwtTokenProvider.validateToken(token);
//
//		// 검증
//		assertFalse(isValid);
//	}
//}