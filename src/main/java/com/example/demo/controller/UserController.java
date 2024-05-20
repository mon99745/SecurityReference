package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * User - Controller for page
 */
@Controller
public class UserController {
	@GetMapping("/login-page")
	public String login() {
		return "login-page";
	}

	@GetMapping("login-redirect-page")
	public String oauth2Login() {
		return "login-redirect-page";
	}

	@GetMapping("/signup")
	public String signup() {
		return "signup";
	}
}