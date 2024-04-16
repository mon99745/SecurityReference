package com.example.demo.repository;

import com.example.demo.domain.Status;
import com.example.demo.domain.ValidToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidTokenRepository extends JpaRepository<ValidToken, Long> {
	Status findByAccessToken(String accessToken);
}
