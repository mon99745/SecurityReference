package com.example.demo.config;


import com.example.demo.filter.JwtAuthenticationFilter;
import com.example.demo.service.OAuth2Service;
import com.example.demo.service.TokenService;
import com.example.demo.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.httpBasic().disable()
				.csrf().disable() // CSRF 보안 비활성화
				.headers().frameOptions().disable() // X-Frame-Options 비활성화

				.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				.and()
				.authorizeRequests()
				.antMatchers(
						"/", "/signup", "/error",
						"/css/**", "/img/**", "/js/**", "/user/**", "/test/**",
						"/h2-console/**", "/login-page", "/oauth/**")
				.permitAll()
				.antMatchers("/index-test-case*").authenticated()
				.anyRequest().authenticated()

				.and()
				.formLogin()
				.loginPage("/login-page");

				configureJwt(http);
				configureOAuth2(http);

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
				.defaultSuccessUrl("/", true)
				.userInfoEndpoint()
				.userService(oAuth2Service);
	}

	@Bean //@Bean을 통해 비밀번호 암호화 스프링 부트 2.0부터는 필수
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}