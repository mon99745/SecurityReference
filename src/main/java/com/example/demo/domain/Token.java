package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Date;

/**
 * Token - DTO & Entity
 */
@Builder
@Data
@AllArgsConstructor
public class Token {
	private String grantType;
	private String accessToken;
	private String refreshToken;


	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Entity
	@Builder
	@Table(name = "ValidToken")
	public static class ValidToken {

		@Id
		private Long id;
		private String accessToken;
		private String refreshToken;
		private Date expireDate;

		@Enumerated(EnumType.STRING)
		private Status status;
	}
}