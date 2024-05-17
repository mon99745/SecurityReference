package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * TEST Controller for index-page
 */
@Controller
public class IndexController {
	@GetMapping("/")
	public String index() {
		return "index";
	}
	@GetMapping("oauthLogin-page")
	public String oauthLogin_page() {
		return "oauthLogin-page";
	}
	@GetMapping("/index-test-case1")
	public String index_test_case_1() {
		return "index-test-case1";
	}

	@GetMapping("/index-test-case2")
	public String index_test_case_2() {
		return "index-test-case2";
	}

	@GetMapping("/index-test-case3")
	public String index_test_case_3() {
		return "index-test-case3";
	}
}