package com.example.demo.controller;

import com.example.demo.domain.Token;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

/**
 * User - HTTP Controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(UserRestController.PATH)
public class UserRestController {
	public static final String PATH = "/user";
	private final UserService userService;

	/**
	 * 로그인
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	@PostMapping("login")
	public Token login(String username, String password) {
		return userService.login(username, password);
	}

	/**
	 * 로그아웃
	 *
	 * @param httpServletRequest
	 */
	@PostMapping("/logout")
	public void logout(HttpServletRequest httpServletRequest) {
		userService.logout(httpServletRequest);
	}

	/**
	 * 회원가입
	 *
	 * @param createMsg
	 * @return
	 */
	@PostMapping("create")
	public User create(@RequestBody User createMsg) {
		return userService.create(createMsg);
	}

	/**
	 * 회원조회
	 *
	 * @param username
	 * @return
	 */
	@GetMapping("read/{username}")
	public Optional<User> read(@PathVariable String username) {
		return userService.read(username);
	}
}