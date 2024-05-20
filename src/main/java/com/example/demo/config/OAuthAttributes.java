package com.example.demo.config;

import com.example.demo.domain.User;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {

	GOOGLE("google", (attribute) -> {
		User userProfile = new User();
		userProfile.setName((String)attribute.get("name"));
		userProfile.setUsername((String)attribute.get("email"));

		return userProfile;
	}),

	NAVER("naver", (attribute) -> {
		User userProfile = new User();

		Map<String, String> responseValue = (Map)attribute.get("response");

		userProfile.setName(responseValue.get("name"));
		userProfile.setUsername(responseValue.get("email"));

		return userProfile;
	});

	private final String registrationId;
	private final Function<Map<String, Object>, User> of; // 로그인한 사용자의 정보를 통하여 UserProfile을 가져옴

	OAuthAttributes(String registrationId, Function<Map<String, Object>, User> of) {
		this.registrationId = registrationId;
		this.of = of;
	}

	public static User extract(String registrationId, Map<String, Object> attributes) {
		return Arrays.stream(values())
				.filter(value -> registrationId.equals(value.registrationId))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new)
				.of.apply(attributes);
	}
}
