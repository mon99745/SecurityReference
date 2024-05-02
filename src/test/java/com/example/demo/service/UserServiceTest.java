package com.example.demo.service;

import com.example.demo.domain.Status;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
	@InjectMocks
	private UserService userService;

	private static final String username = "test_user";
	private static final String password = "test_1234";
	private static final String accessToken = "testAccessToken";
	private static final List<String> roles = Arrays.asList("ROLE_USER");

	@Test
	void testLogout() {
		// 준비
		String accessToken = "testAccessToken";

		// 가짜 request 설정
		when(tokenService.getAccessToken(request))
				.thenReturn(accessToken);

		// 실행
		userService.logout(request);

		// 검증
		verify(tokenService).getAccessToken(request);
		verify(tokenService).updateStatusToken(accessToken, Status.INVALID);
	}

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

	@Test
	public void testWithdraw() {
		// 준비
		UserDetails userDetails = mock(UserDetails.class);
		User user =
				new User(1L, username, password, roles);
		Optional<User> optionalUser = Optional.of(user);
		when(tokenService.getAccessToken(any(HttpServletRequest.class)))
				.thenReturn(accessToken);
		when(jwtTokenProvider.getAuthentication(accessToken))
				.thenReturn(mock(Authentication.class));
		when(jwtTokenProvider.getAuthentication(accessToken).getPrincipal())
				.thenReturn(userDetails);
		when(userDetails.getUsername()).thenReturn(username);
		when(userRepository.findByUsername(username))
				.thenReturn(optionalUser);

		// 실행
		userService.withdraw(mock(HttpServletRequest.class));

		// 검증
		verify(userRepository).delete(user);
		verify(tokenService).updateStatusToken(accessToken, Status.REVOKED);
	}

	@Test
	public void testWithdraw_Exception() {
		// 준비
		when(tokenService.getAccessToken(any(HttpServletRequest.class)))
				.thenThrow(new RuntimeException("Invalid token"));

		// 실행 및 검증
		assertThrows(RuntimeException.class, () -> {
			userService.withdraw(mock(HttpServletRequest.class));
		});
	}
}