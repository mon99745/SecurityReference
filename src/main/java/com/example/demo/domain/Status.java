package com.example.demo.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Status - Enum
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Status {
	/**
	 * 유효 상태
	 */
	VALID("유효"),
	/**
	 * 무효 상태
	 */
	INVALID("무효"),
	/**
	 * 폐기 상태
	 */
	REVOKED("폐기");

	public static final String ENUM = "";
	public static final String DESC = "상태 (VALID:유효, INVALID: 무효, REVOKED: 폐기)";
	public static final String DEFAULT = "'VALID'";

	private final String value;
}
