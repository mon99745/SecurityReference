package com.example.demo.service;

import com.example.demo.domain.Status;
import com.example.demo.domain.Token;
import com.example.demo.domain.User;
import com.example.demo.repository.TokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.MethodName.class)
@ActiveProfiles("test")
class UserServiceTest {
	@Mock
	private JwtTokenProvider jwtTokenProvider;
	@Mock
	private HttpServletRequest request;
	@Mock
	private UserRepository userRepository;
	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Mock
	private TokenService tokenService;
	@Mock
	private TokenRepository tokenRepository;
	@Autowired
	private UserService userService;

	private static final String username = "test_user";
	private static final String password = "test_1234";
	private static final List<String> roles = Arrays.asList("ROLE_USER");

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	/**
	 * @Desc 로그아웃 테스트
	 * @throws InterruptedException
	 */
	@Test
	public void testLogout() throws InterruptedException {
		// Arrange
		String testAccessToken = "Bearer testAccessToken";
		String testRefreshToken = "Bearer testRefreshToken";

		when(request.getHeader("Authorization")).thenReturn(testAccessToken);
		when(request.getHeader("X-Refresh-Token")).thenReturn(testRefreshToken);

		// Act
		userService.logout(request);
		Thread.sleep(3000);

		// Assert
//		verify(tokenService).updateStatusToken(any(Token.class), eq(Status.INVALID));
	}

	/**
	 * @Desc 회원 가입 테스트
	 * case : 이미 회원 정보가 존재하지 않는 경우
	 */
	@Test
	public void testCreate_WhenUserDoesNotExist() {
		// 준비
		User createUser =
				new User(1L, username, password, roles);
		when(userRepository.findByUsername(createUser.getUsername()))
				.thenReturn(Optional.empty());
		when(bCryptPasswordEncoder.encode(createUser.getPassword()))
				.thenReturn("hashedPassword");

		// 실행
		User createdUser = userService.create(createUser);

		// 검증
		assertEquals(createUser.getUsername(), createdUser.getUsername());
		assertEquals("hashedPassword", createdUser.getPassword());
		verify(userRepository).save(createUser);
	}

	/**
	 * @Desc 회원 가입 테스트
	 * case : 이미 회원 정보가 존재하는 경우
	 */
	@Test
	public void testCreate_WhenUserExists() {
		// 준비
		User existingUser =
				new User(1L, "existing_User", "existing_Password", roles);
		when(userRepository.findByUsername(existingUser.getUsername()))
				.thenReturn(Optional.of(existingUser));

		// 실행 및 검증
		assertThrows(RuntimeException.class, () -> {
			userService.create(existingUser);
		});
	}

	/**
	 * @Desc 회원 조회 테스트
	 * case : 회원 정보가 존재할 경우
	 */
	@Test
	public void testRead_WhenUserExists() {
		// 준비
		String username = "existingUser";
		User existingUser = new User(1L, username, "existing_Password", roles);
		when(userRepository.findByUsername(username))
				.thenReturn(Optional.of(existingUser));

		// 실행
		Optional<User> user = userService.read(username);

		// 검증
		assertTrue(user.isPresent());
		assertEquals(existingUser, user.get());
	}

	/**
	 * @Desc 회원 조회 테스트
	 * case : 회원 정보가 존재하지 않는 경우
	 */
	@Test
	public void testRead_WhenUserDoesNotExist() {
		// 준비
		String username = "nonExistingUser";
		when(userRepository.findByUsername(username))
				.thenReturn(Optional.empty());

		// 실행 및 검증
		assertThrows(RuntimeException.class, () -> {
			userService.read(username);
		});
	}

	/**
	 * @Desc 회원 탈퇴 테스트
	 */
	@Test
	public void testWithdraw() {
		// 준비
		Token token = Token.builder()
				.accessToken("ValidAccessToken")
				.refreshToken("ValidRefreshToken")
				.build();

		UserDetails userDetails = mock(UserDetails.class);
		User user = new User(1L, username, password, roles);
		Optional<User> optionalUser = Optional.of(user);

		when(tokenService.getToken(any(HttpServletRequest.class)))
				.thenReturn(token);
		when(jwtTokenProvider.getAuthentication(token.getAccessToken()))
				.thenReturn(mock(Authentication.class));
		when(jwtTokenProvider.getAuthentication(token.getAccessToken()).getPrincipal())
				.thenReturn(userDetails);
		when(userDetails.getUsername()).thenReturn(username);
		when(userRepository.findByUsername(username))
				.thenReturn(optionalUser);

		// 실행
		userService.withdraw(mock(HttpServletRequest.class));

		// 검증
		verify(userRepository).delete(user);
		verify(tokenService).updateStatusToken(token, Status.REVOKED);
	}
}