package com.example.demo.controller;

import com.example.demo.config.annotation.ControllerTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.util.HashMap;
import java.util.Map;

import static com.example.demo.controller.UserRestController.PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ControllerTest
@DisplayName("회원 API 테스트")
class UserRestControllerTest {
	public static final String target = "Controller";

	@Autowired
	private MockMvc mvc;
	private static final MockHttpSession SESSION = new MockHttpSession();
	private static final String username = "test_user";
	private static final String password = "test_1234";
	private static final String name = "USER";
	private static final String ROLE_ADMIN = "ROLE_ADMIN";
	private static final String ROLE_USER = "ROLE_USER";
	private static Map<String, String> fMap = new HashMap<>();
	private static final String createMsg = "{\n" +
			"    \"username\" : \"" + username + "\",\n" +
			"    \"password\" : \"" + password + "\",\n" +
			"    \"name\" : \"" + name + "\",\n" +
			"    \"roles\": [\"" + ROLE_USER + "\"]\n" +
			"}";

	@Test
	@DisplayName("[" + target + "] 회원 가입 테스트")
	void t01create() throws Exception {
		mvc.perform(post(PATH + "/create")
						.session(SESSION)
						.contentType(MediaType.APPLICATION_JSON)
						.content(createMsg))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("[" + target + "] 회원 조회 테스트")
	void t02read() throws Exception {
		t01create();
		mvc.perform(get(PATH + "/read/{username}", username)
						.session(SESSION)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("[" + target + "] 로그인 테스트")
	void t03login() throws Exception {
		t01create();
		mvc.perform(post(PATH + "/login")
						.session(SESSION)
						.contentType(MediaType.APPLICATION_JSON)
						.param("username", username)
						.param("password", password))
				.andDo(print())
				.andDo(r -> {
					fMap = tokenJsonParser(r.getResponse().getContentAsString());
				})
				.andExpect(MockMvcResultMatchers.status().isOk());
	}


	@Test
	@DisplayName("[" + target + "] 로그아웃 테스트")
	void t04logout() throws Exception {
		t03login();
		mvc.perform(post(PATH + "/logout")
						.session(SESSION)
						.header("Authorization", fMap.get("grantType") + " " + fMap.get("accessToken"),
								"X-Refresh-Token", fMap.get("grantType") + " " + fMap.get("refreshToken")
						)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("[" + target + "] 회원 탈퇴 테스트")
	void t05withdraw() throws Exception {
		t03login();
		Thread.sleep(1000);
		mvc.perform(post(PATH + "/withdraw")
						.session(SESSION)
						.header("Authorization", fMap.get("grantType") + " " + fMap.get("accessToken"),
								"X-Refresh-Token", fMap.get("grantType") + " " + fMap.get("refreshToken")
						).contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	/**
	 * @param jsonStr
	 * @return
	 * @Desc String -> fMap(field Map)
	 */
	Map<String, String> tokenJsonParser(String jsonStr) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode jsonNode = objectMapper.readTree(jsonStr);
			fMap.put("grantType", jsonNode.get("grantType").asText());
			fMap.put("accessToken", jsonNode.get("accessToken").asText());
			fMap.put("refreshToken", jsonNode.get("refreshToken").asText());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fMap;
	}
}