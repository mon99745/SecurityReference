package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

/**
 * Token - Entity for token validation
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class ValidToken {

	@Id
	private String accessToken;

	@Enumerated(EnumType.STRING)
	private Status status;
}