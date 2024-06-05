package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

/**
 * User - Controller for page
 */
@Controller
public class UserController {
	@GetMapping("login")
	public String login() {
		return "login-page";
	}

	@GetMapping("login-reconfirm")
	public String login(Model model, HttpSession session) {
		model.addAttribute("username", session.getAttribute("username"));
		return "login-reconfirm-page";
	}

	@GetMapping("signup")
	public String signup() {
		return "signup-page";
	}
}