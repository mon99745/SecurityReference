package com.example.demo.repository;

import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User - Repository
 */
public interface UserRepository
		extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	Optional<User> findUserByUsernameAndProvider(String username, String provider);
}