package com.example.demo.service;

import com.example.demo.domain.OAuthAttributes;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final UserRepository userRepository;

	/**
	 * OAuth2.0 인증 프로세스에서 사용자의 정보를 가져와 처리
	 * @param userRequest the user request
	 * @return
	 * @throws OAuth2AuthenticationException
	 */
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 로그인을 수행한 서비스의 이름

		String userNameAttributeName = userRequest
				.getClientRegistration()
				.getProviderDetails()
				.getUserInfoEndpoint()
				.getUserNameAttributeName(); // PK가 되는 정보

		Map<String, Object> attributes = oAuth2User.getAttributes(); // 사용자가 가지고 있는 정보

		User userProfile = OAuthAttributes.extract(registrationId, attributes);
		userProfile.setProvider(registrationId);

		updateOrSaveUser(userProfile);

		Map<String, Object> customAttribute =
				getCustomAttribute(registrationId, userNameAttributeName,
						attributes, userProfile, String.valueOf(userRequest.getAccessToken().getTokenValue()));

		return new DefaultOAuth2User(
				Collections.singleton(new SimpleGrantedAuthority("USER")),
				customAttribute,
				userNameAttributeName);
	}

	public Map<String, Object> getCustomAttribute(String registrationId,
								  String userNameAttributeName,
								  Map<String, Object> attributes,
								  User userProfile, String accessToken) {
		Map<String, Object> customAttribute = new ConcurrentHashMap<>();

		customAttribute.put(userNameAttributeName, attributes.get(userNameAttributeName));
		customAttribute.put("provider", registrationId);
		customAttribute.put("username", userProfile.getUsername());
		customAttribute.put("name", userProfile.getName());

		return customAttribute;
	}

	public User updateOrSaveUser(User userProfile) {
		SecureRandom random = new SecureRandom();
		Optional<User> existingUser = userRepository
				.findUserByUsernameAndProvider(userProfile.getUsername(), userProfile.getProvider());

		if (existingUser.isPresent()) {
			return existingUser.get();
		} else {
			User newUser = User.builder()
					.id(1L)
					.username(userProfile.getUsername())
					.password(String.valueOf(random.nextInt()))
					.name(userProfile.getName())
					.provider(userProfile.getProvider())
					.roles(Arrays.asList("ROLE_USER"))
					.build();

			return userRepository.save(newUser);
		}
	}
}