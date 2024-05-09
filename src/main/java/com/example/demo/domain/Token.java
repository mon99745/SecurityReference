package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Token - DTO & Entity
 */
@Builder
@Data
@AllArgsConstructor
public class Token {
	/**
	 * 토큰 타입
	 */
	private String grantType;

	/**
	 * 인증 토큰
	 */
	private String accessToken;

	/**
	 * 재발급 토큰
	 */
	private String refreshToken;


	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Setter
	@Entity
	@Builder
	@Table(name = "ValidToken")
	public static class ValidToken {
		/**
		 * 토큰 식별 번호
		 */
		@Id
		@Column(nullable = false)
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		/**
		 * 인증 토큰
		 */
		private String accessToken;

		/**
		 * 재발급 토큰
		 */
		private String refreshToken;

		/**
		 * 토큰 상태 정보
		 */
		@Enumerated(EnumType.STRING)
		private Status status;
	}
}