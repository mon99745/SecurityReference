package com.example.demo.service;

import com.example.demo.domain.Status;
import com.example.demo.domain.Token;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

/**
 * User - Main elements of service
 */
@Service
@RequiredArgsConstructor
public class UserService {
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtTokenProvider jwtTokenProvider;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserRepository userRepository;
	private final TokenService tokenService;

	@Transactional
	public Token login(String username, String password) {
		// 1. Login ID/PW 를 기반으로 Authentication 객체 생성
		// 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
		UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(username, password);

		// 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
		// authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
		Authentication authentication = authenticationManagerBuilder
				.getObject()
				.authenticate(authenticationToken);

		// 3. 인증 정보를 기반으로 JWT 토큰 생성
		Token token = jwtTokenProvider.generateToken(authentication);

		return token;
	}

	@Transactional
	public void logout(HttpServletRequest request) {
		String accessToken = tokenService.getAccessToken(request);
		tokenService.updateStatusToken(accessToken, Status.INVALID);
	}

	@Transactional
	public User create(User createMsg) {
		if (userRepository.findByUsername(createMsg.getUsername()).isPresent()) {
			throw new RuntimeException("이미 존재하는 아이디입니다.");
		} else {
			String hashPw = bCryptPasswordEncoder.encode(createMsg.getPassword());
			createMsg.setPassword(hashPw);
			userRepository.save(createMsg);
			return createMsg;
		}
	}

	public Optional<User> read(String username) {
		return userRepository.findByUsername(username);
	}

	@Transactional
	public void withdraw(HttpServletRequest request) {
		try {
			// getAccessToken in header
			String accessToken = tokenService.getAccessToken(request);

			// getUsername in Token
			UserDetails userDetails = (UserDetails) jwtTokenProvider
					.getAuthentication(accessToken)
					.getPrincipal();
			String username = userDetails.getUsername();

			Optional<User> optionalUser = userRepository.findByUsername(username);
			userRepository.delete(optionalUser.orElse(null));

			tokenService.updateStatusToken(accessToken, Status.REVOKED);
		} catch (Exception e) {
			throw new RuntimeException("회원 탈퇴 중 예외가 발생하였습니다. ");
		}
	}
}