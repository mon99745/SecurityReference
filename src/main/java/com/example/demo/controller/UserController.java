package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * User - Controller for page
 */
@Controller
@RequestMapping(UserController.PATH)
public class UserController {
	public static final String PATH = "/auth";

	@GetMapping("login")
	public String login() {
		return "login-page";
	}

	/**
	 * OAuth2.0 의 경우만 시행
	 * 로그인 재차 확인하며 리다이렉트 보내도록
	 *
	 * @param model
	 * @param session
	 * @return
	 */
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