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

	@GetMapping("/index-test")
	public String index_test() {
		return "index-test";
	}
}
