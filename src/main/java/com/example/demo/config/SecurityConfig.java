package com.example.demo.config;


import com.example.demo.filter.JwtAuthenticationFilter;
import com.example.demo.service.OAuth2Service;
import com.example.demo.service.TokenService;
import com.example.demo.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring security config
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtTokenProvider jwtTokenProvider;
	private final TokenService tokenService;
	private final OAuth2Service oAuth2Service;

	@Value("${spring.security.oauth2-enabled:false}")
	private boolean oauth2Enabled;

	private static final String[] PERMIT_URL = {
			"/",
			"/signup",
			"/error/**",
			"/user/**",
			"/h2-console/**"
	};

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.httpBasic().disable()
				.csrf().disable()
				.headers().frameOptions().disable()

				.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				.and()
				.authorizeRequests()
				.antMatchers(PERMIT_URL).permitAll()
				.antMatchers("/index-test-case1").authenticated()
				.antMatchers("/index-test-case2").hasRole("USER")
				.antMatchers("/index-test-case3").hasRole("ADMIN")
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.anyRequest().authenticated()

				.and()
				.formLogin().permitAll()
				.loginPage("/login-page");

		configureJwt(http);
		if (oauth2Enabled) {
			configureOAuth2(http);
		}

		return http.build();
	}

	private void configureJwt(HttpSecurity http) {
		http
				.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, tokenService),
						UsernamePasswordAuthenticationFilter.class);
	}

	private void configureOAuth2(HttpSecurity http) throws Exception {
		http
				.oauth2Login()
				.loginPage("/login-page")
				.defaultSuccessUrl("/login-redirect-page", true)
				.userInfoEndpoint()
				.userService(oAuth2Service);
	}
}