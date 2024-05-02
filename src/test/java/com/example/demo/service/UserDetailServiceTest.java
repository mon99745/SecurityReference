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

	@Test
	public void testLoadUserByUsername_UserFound() {
		// 준비

		User user = new User(1L, username, password, roles);
		when(userRepository.findByUsername(username))
				.thenReturn(Optional.of(user));

		// 실행
		UserDetails userDetails = userDetailService.loadUserByUsername(username);

		// 검증
		assertNotNull(userDetails);
		assertEquals(username, userDetails.getUsername());
	}

	@Test
	public void testLoadUserByUsername_UserNotFound() {
		// 준비
		String username = "nonExistingUser";
		when(userRepository.findByUsername(username))
				.thenReturn(Optional.empty());

		// 실행 및 검증
		assertThrows(UsernameNotFoundException.class, () -> {
			userDetailService.loadUserByUsername(username);
		});
	}
}