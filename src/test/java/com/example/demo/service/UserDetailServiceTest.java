package com.example.demo.service;

import com.example.demo.config.annotation.ServiceTest;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ServiceTest
@DisplayName("회원 상세 서비스 테스트")
class UserDetailServiceTest {
	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private UserDetailService userDetailService;
	private static final String username = "test_user";
	private static final String password = "test_1234";
	private static final String name = "USER";
	private static final String provier = null;
	private static final List<String> roles = Arrays.asList("ROLE_USER");

	@Test
	@DisplayName("회원 상세 정보 검색 테스트")
	public void testLoadUserByUsername_UserFound() {
		// Arrange
		User user = new User(1L, username, password, name, provier, roles);
		when(userRepository.findByUsername(username))
				.thenReturn(Optional.of(user));

		// Act
		UserDetails userDetails = userDetailService.loadUserByUsername(username);

		// Assert
		assertNotNull(userDetails);
		assertEquals(username, userDetails.getUsername());
	}

	@Test
	@DisplayName("회원 상세 정보 검색 테스트_예외")
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