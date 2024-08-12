package im.toduck.global.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.fasterxml.jackson.databind.ObjectMapper;

import im.toduck.global.security.filter.JwtAuthenticationFilter;
import im.toduck.global.security.filter.JwtExceptionFilter;
import im.toduck.global.security.jwt.JwtProvider;
import im.toduck.infra.redis.forbidden.ForbiddenTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SecurityFilterConfig {
	private final ObjectMapper objectMapper;

	private final UserDetailsService userDetailServiceImpl;
	private final JwtProvider accessTokenProvider;
	private final ForbiddenTokenService forbiddenTokenService;

	@Bean
	public JwtExceptionFilter jwtExceptionFilter() {
		return new JwtExceptionFilter(objectMapper);
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(userDetailServiceImpl, accessTokenProvider, forbiddenTokenService);
	}
}
