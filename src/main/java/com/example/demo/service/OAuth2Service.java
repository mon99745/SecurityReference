package com.example.demo.service;

import com.example.demo.config.OAuth2Attributes;
import com.example.demo.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final UserService userService;
	private final HttpSession httpSession;

	/**
	 * OAuth2.0 인증 프로세스에서 사용자의 정보를 가져와 처리
	 *
	 * @param userRequest the user request
	 * @return
	 * @throws OAuth2AuthenticationException
	 */
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

		// 1. 로그인을 수행한 서비스의 이름 추출
		String registrationId = userRequest.getClientRegistration().getRegistrationId();

		// 2. 사용자가 가지고 있는 정보 추출
		Map<String, Object> attributes = oAuth2User.getAttributes();

		// 3. 임시 비밀번호 발급
		String tmpPassword = String.valueOf(new SecureRandom().nextInt());

		User userProfile = OAuth2Attributes.extract(registrationId, attributes);
		userProfile.setProvider(registrationId);
		userProfile.setPassword(tmpPassword);
		userProfile.setRoles(Collections.singletonList("ROLE_USER"));

		// 4. 간편 회원 가입
		userService.create(userProfile);

		// 5. 아이디, 임시 비밀번호 Session 저장
		httpSession.setAttribute("username", userProfile.getUsername());
		httpSession.setAttribute("tmpPassword", tmpPassword);

		return oAuth2User;
	}
}