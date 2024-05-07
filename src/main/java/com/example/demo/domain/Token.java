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
	@Setter
	@Entity
	@Builder
	@Table(name = "ValidToken")
	public static class ValidToken {

		@Id
		@Column(nullable = false)
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;
		private String accessToken;
		private String refreshToken;

		@Enumerated(EnumType.STRING)
		private Status status;
	}
}