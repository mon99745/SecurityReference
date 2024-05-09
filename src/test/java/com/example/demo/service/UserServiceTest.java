package com.example.demo.service;

import com.example.demo.domain.Token;
import com.example.demo.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@DisplayName("회원 서비스 테스트")
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.MethodName.class)
@Transactional
@ActiveProfiles("test")
class UserServiceTest {
	public static final String target = "Service";
	@Mock
	private HttpServletRequest request;
	@Autowired
	private UserService userService;
	private static User createdUser = null;
	private static Token token = null;
	private static final String username = "test_user";
	private static final String password = "test_1234";
	private static final List<String> roles = Arrays.asList("ROLE_USER");
	private static String testAccessToken = "Bearer testAccessToken";
	private static String testRefreshToken = "Bearer testRefreshToken";

	@Test
	@DisplayName("[" + target + "] 로그인 테스트")
	public void testLogin() {
		// Arrange
		testCreate();

		// Act
		token = userService.login(username, password);
		testAccessToken = token.getAccessToken();
		testRefreshToken = token.getRefreshToken();

		// Assert
		assertNotNull(token);
		assertNotNull(token.getGrantType());
		assertNotNull(testAccessToken);
		assertNotNull(testRefreshToken);

	}

	@Test
	@DisplayName("[" + target + "] 로그아웃 테스트")
	public void testLogout() throws InterruptedException {
		// Arrange
		testLogin();

		when(request.getHeader("Authorization")).thenReturn(testAccessToken);
		when(request.getHeader("X-Refresh-Token")).thenReturn(testRefreshToken);

		// Act
		boolean result = userService.logout(request);
		Thread.sleep(3000);

		// Assert
		assertEquals(result, true);
	}

	@Test
	@DisplayName("[" + target + "] 회원 가입 테스트")
	public void testCreate() {
		// Arrange
		User createMsg = User.builder()
				.username(username)
				.password(password)
				.roles(roles)
				.build();

		// Act
		createdUser = userService.create(createMsg);

		// Assert
		assertNotNull(createdUser);
		assertEquals(createMsg.getUsername(), createdUser.getUsername());
		assertEquals(createMsg.getPassword(), createdUser.getPassword());
		assertEquals(createMsg.getRoles(), createdUser.getRoles());
	}

	@Test
	@DisplayName("[" + target + "] 회원 가입 테스트_예외")
	public void testCreate_WhenUserExists() {
		// Arrange
		User createMsg = User.builder()
				.username(username)
				.password(password)
				.roles(roles)
				.build();

		userService.create(createMsg);

		// Act & Assert
		assertThrows(RuntimeException.class, () -> userService.create(createMsg));
	}

	@Test
	@DisplayName("[" + target + "] 회원 조회 테스트")
	public void testRead() {
		// Arrange
		testCreate();

		// Act
		Optional<User> readUser = userService.read(username);

		//Assert
		assertTrue(createdUser.equals(readUser.get()));
	}

	@Test
	@DisplayName("[" + target + "] 회원 조회 테스트_예외")
	public void testRead_WhenUserDoesNotExist() {
		//Act $ Assert
		assertThrows(RuntimeException.class, () -> userService.read(username));
	}

	@Test
	@DisplayName("[" + target + "] 회원 탈퇴 테스트")
	public void testWithdraw() {
		// Arrange
		testLogin();

		when(request.getHeader("Authorization")).thenReturn(testAccessToken);
		when(request.getHeader("X-Refresh-Token")).thenReturn(testRefreshToken);

		// Act
		boolean result = userService.withdraw(request);

		// Assert
		assertEquals(result, true);
	}
}