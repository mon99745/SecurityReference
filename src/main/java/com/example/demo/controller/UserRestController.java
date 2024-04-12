package com.example.demo.controller;

import com.example.demo.domain.TokenInfo;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(UserRestController.PATH)
public class UserRestController {
	public static final String PATH = "/user";
	private final UserRepository userRepository;
	private final UserService userService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	/**
	 * 로그인
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	@PostMapping("login")
	public TokenInfo login(String username, String password) {
		return userService.login(username, password);
	}

	/**
	 * 회원가입
	 *
	 * @param createMsg
	 * @return
	 */
	@PostMapping("create")
	public User create(@RequestBody User createMsg) {
		String hashPw = bCryptPasswordEncoder.encode(createMsg.getPassword());
		createMsg.setPassword(hashPw);
		userRepository.save(createMsg);
		return createMsg;
	}

	/**
	 * 회원조회
	 *
	 * @param username
	 * @return
	 */
	@GetMapping("read/{username}")
	public Optional<User> read(@PathVariable String username) {
		return userRepository.findByUsername(username);
	}
}