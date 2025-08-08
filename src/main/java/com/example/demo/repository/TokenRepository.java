package com.example.demo.repository;

import com.example.demo.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Token - Repository for validation
 */
@Repository
public interface TokenRepository
		extends JpaRepository<Token.ValidToken, Long> {
	Optional<Token.ValidToken> findByAccessToken(String accessToken);
	Optional<Token.ValidToken> findByRefreshToken(String refreshToken);
}