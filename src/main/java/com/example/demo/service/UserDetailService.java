package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * User - Additional elements of service
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
	private final UserRepository userRepository;

	/**
	 * Username을 DB에서 조회 및 UserDetails 반환
	 *
	 * @param username the username identifying the user whose data is required.
	 * @return
	 * @throws UsernameNotFoundException
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			return userRepository.findByUsername(username)
					.map(this::createUserDetails)
					.orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
		} catch (UsernameNotFoundException ex) {
			log.info(ex.getMessage() + " ID : " + username);
			throw ex;
		}
	}

	/**
	 * UserDetails Object 생성
	 *
	 * @param user
	 * @return
	 */
	private UserDetails createUserDetails(User user) {
		return User.builder()
				.username(user.getUsername())
				.password(user.getPassword())
				.roles(Arrays.asList(user.getRoles().toArray(new String[0])))
				.build();
	}
}