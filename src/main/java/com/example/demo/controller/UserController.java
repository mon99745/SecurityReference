package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
	@GetMapping("/login-page")
	public String login() {
		return "login-page";
	}

	@GetMapping("/signup")
	public String signup() {
		return "signup";
	}
}
