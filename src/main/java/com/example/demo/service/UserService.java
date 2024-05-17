package com.example.demo.service;

import com.example.demo.domain.Status;
import com.example.demo.domain.Token;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtTokenProvider;
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

	/**
	 * 로그인 처리
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	@Transactional
	public Token login(String username, String password) {
		// 1. Login ID/PW 를 기반으로 Authentication 객체 생성
		// 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
		UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(username, password);

		// 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
		// authenticate 매서드가 실행될 때 UserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
		Authentication authentication = authenticationManagerBuilder
				.getObject()
				.authenticate(authenticationToken);

		// 3. 인증 정보를 기반으로 JWT 토큰 생성
		Token token = jwtTokenProvider.generateToken(authentication);

		return token;
	}

	/**
	 * 로그아웃 처리
	 *
	 * @param request
	 * @return
	 */
	@Transactional
	public boolean logout(HttpServletRequest request) {
		Token token = tokenService.getToken(request);
		boolean result = tokenService.updateStatusToken(token, Status.INVALID);
		return result;
	}

	/**
	 * 회원 가입 처리
	 *
	 * @param createMsg
	 * @return
	 */
	@Transactional
	public User create(User createMsg) {
		if (createMsg.getProvider().isEmpty() &&
				userRepository.findByUsername(createMsg.getUsername()).isPresent()) {
			throw new RuntimeException("이미 존재하는 아이디입니다.");
		} else {
			String hashPw = bCryptPasswordEncoder.encode(createMsg.getPassword());
			createMsg.setPassword(hashPw);
			userRepository.save(createMsg);
			return createMsg;
		}
	}

	/**
	 * 회원 조회 처리
	 *
	 * @param username
	 * @return
	 */
	public Optional<User> read(String username) {
		Optional<User> user = userRepository.findByUsername(username);
		if (!user.isPresent()) {
			throw new RuntimeException("해당 회원의 정보가 없습니다. ID: " + username);
		}
		return user;
	}

	/**
	 * 회원 탈퇴 처리
	 *
	 * @param request
	 * @return
	 */
	@Transactional
	public boolean withdraw(HttpServletRequest request) {
		boolean result = false;
		try {
			// getToken in header
			Token token = tokenService.getToken(request);

			// getUsername in Token
			UserDetails userDetails = (UserDetails) jwtTokenProvider
					.getAuthentication(token.getAccessToken(), null)
					.getPrincipal();

			String username = userDetails.getUsername();
			Optional<User> user = userRepository.findByUsername(username);
			userRepository.delete(user.orElse(null));

			result = tokenService.updateStatusToken(token, Status.REVOKED);
		} catch (Exception e) {
			throw new RuntimeException("회원 탈퇴 중 예외가 발생하였습니다. ");
		}
		return result;
	}
}