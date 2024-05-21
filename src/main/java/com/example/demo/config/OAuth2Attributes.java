package com.example.demo.config;

import com.example.demo.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public enum OAuth2Attributes {

	GOOGLE("google", (attribute) -> {
		User userProfile = new User();
		userProfile.setName((String) attribute.get("name"));
		userProfile.setUsername((String) attribute.get("email"));

		return userProfile;
	}),

	NAVER("naver", (attribute) -> {
		User userProfile = new User();

		Map<String, String> responseValue = (Map) attribute.get("response");

		userProfile.setName(responseValue.get("name"));
		userProfile.setUsername(responseValue.get("email"));

		return userProfile;
	});

	private final String registrationId;
	private final Function<Map<String, Object>, User> of;

	public static User extract(String registrationId, Map<String, Object> attributes) {
		return Arrays.stream(values())
				.filter(value -> registrationId.equals(value.registrationId))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new)
				.of.apply(attributes);
	}
}