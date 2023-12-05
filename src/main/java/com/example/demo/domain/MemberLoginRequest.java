package com.example.demo.domain;

import lombok.Data;

@Data
public class MemberLoginRequest {
	private String memberId;
	private String password;
}
