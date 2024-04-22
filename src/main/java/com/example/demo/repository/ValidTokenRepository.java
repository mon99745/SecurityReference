package com.example.demo.repository;

import com.example.demo.domain.ValidToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Token - Repository for validation
 */
public interface ValidTokenRepository extends JpaRepository<ValidToken, String> {
	ValidToken findByAccessToken(String accessToken);
}
