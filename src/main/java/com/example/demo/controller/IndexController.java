package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * TEST Controller for index-page
 */
@Slf4j
@Controller
public class IndexController {
	@GetMapping
	public String index() {
		return "index";
	}

	@GetMapping("index-test-case1")
	public String index_test_case_1(Authentication authentication, Model model) {
		log.info("User Role = " + authentication.getAuthorities());

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		model.addAttribute("username", userDetails.getUsername());
		model.addAttribute("role", authentication.getAuthorities());
		return "index-test-page1";
	}

	@GetMapping("index-test-case2")
	public String index_test_case_2(Authentication authentication, Model model) {
		log.info("User Role = " + authentication.getAuthorities());

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		model.addAttribute("username", userDetails.getUsername());
		model.addAttribute("role", authentication.getAuthorities());
		return "index-test-page2";
	}

	@GetMapping("index-test-case3")
	public String index_test_case_3(Authentication authentication, Model model) {
		log.info("User Role = " + authentication.getAuthorities());

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		model.addAttribute("username", userDetails.getUsername());
		model.addAttribute("role", authentication.getAuthorities());
		return "index-test-page3";
	}
}