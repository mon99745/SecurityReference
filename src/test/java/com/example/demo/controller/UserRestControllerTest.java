package com.example.demo.controller;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import static com.example.demo.controller.UserRestController.PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.MethodName.class)
class UserRestControllerTest {
	@Autowired
	private MockMvc mvc;
	private static final MockHttpSession SESSION = new MockHttpSession();
	private static final String username = "test_user";
	private static final String password = "test_1234";

	private static final String ROLE_ADMIN = "ROLE_ADMIN";
	private static final String ROLE_USER = "ROLE_USER";
	private static String tokenInfo = "{\"grantType\":\"Bearer\"," +
			"\"accessToken\":\"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZX" +
			"N0X3VzZXIiLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzE0NTU" +
			"yODg1fQ.280TBXC0bh1MeNtDZY_jEthVFJz6bhwcptSQr7yTTlg\"" +
			",\"refreshToken\":\"eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3" +
			"MTQ1NTI4ODV9.3rI1eWOlZe1Nhyclm5i5mylydibp35bnpYwehOdC" +
			"oAc\"}";

	private static final String createMsg = "{\n" +
			"    \"username\" : \"" + username + "\",\n" +
			"    \"password\" : \"" + password + "\",\n" +
			"    \"roles\": [\"" + ROLE_USER + "\"]\n" +
			"}";

	@Test
	void t01create() throws Exception {
		mvc.perform(post(PATH + "/create")
						.session(SESSION)
						.contentType(MediaType.APPLICATION_JSON)
						.content(createMsg))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	void t02read() throws Exception {
		mvc.perform(get(PATH + "/read/{username}", username)
						.session(SESSION)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	void t03login() throws Exception {
		mvc.perform(post(PATH + "/login")
						.session(SESSION)
						.contentType(MediaType.APPLICATION_JSON)
						.param("username", username)
						.param("password", password))
				.andDo(print())
				.andDo(result -> {
					tokenInfo = result.getResponse().getContentAsString();
				})
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

//	@Test
	void t04logout() throws Exception {
		mvc.perform(post(PATH + "/logout")
						.session(SESSION)
						.contentType(MediaType.APPLICATION_JSON)
						.header(tokenInfo))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

//	@Test
	void t05withdraw() throws Exception {
		mvc.perform(post(PATH + "/withdraw")
						.session(SESSION)
						.contentType(MediaType.APPLICATION_JSON)
						.header(tokenInfo))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
}