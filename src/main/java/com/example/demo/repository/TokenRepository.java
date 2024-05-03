package com.example.demo.repository;

import com.example.demo.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Token - Repository for validation
 */
public interface TokenRepository
		extends JpaRepository<Token.ValidToken, Long> {
	Token.ValidToken findByAccessToken(String accessToken);
	Token.ValidToken findByRefreshToken(String refreshToken);
}
