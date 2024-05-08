package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.MethodName.class)
@ActiveProfiles("test")
class UserDetailServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserDetailService userDetailService;

	private static final String username = "test_user";
	private static final String password = "test_1234";
	private static final List<String> roles = Arrays.asList("ROLE_USER");

	/**
	 * @Desc 회원 상세 정보 검색 테스트
	 */
	@Test
	public void testLoadUserByUsername_UserFound() {
		// Arrange
		User user = new User(1L, username, password, roles);
		when(userRepository.findByUsername(username))
				.thenReturn(Optional.of(user));

		// Act
		UserDetails userDetails = userDetailService.loadUserByUsername(username);

		// Assert
		assertNotNull(userDetails);
		assertEquals(username, userDetails.getUsername());
	}

	/**
	 * @Desc 회원 상세 정보 검색 테스트
	 * case: 존재하지 않는 회원일 경우
	 */
	@Test
	public void testLoadUserByUsername_UserNotFound() {
		// Arrange
		String username = "nonExistingUser";
		when(userRepository.findByUsername(username))
				.thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(UsernameNotFoundException.class, () -> {
			userDetailService.loadUserByUsername(username);
		});
	}
}