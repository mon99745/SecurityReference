package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Token - DTO
 */
@Builder
@Data
@AllArgsConstructor
public class TokenInfo {

	private String grantType;
	private String accessToken;
	private String refreshToken;
}