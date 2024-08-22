package im.toduck.global.security.handler;

import static im.toduck.global.exception.ExceptionCode.*;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import im.toduck.global.presentation.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private static final String CHARACTER_ENCODING = "UTF-8";
	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException)
		throws IOException, ServletException {

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(CHARACTER_ENCODING);
		response.setStatus(EMPTY_ACCESS_TOKEN.getHttpStatus().value());
		objectMapper.writeValue(response.getWriter(), ApiResponse.createError(EMPTY_ACCESS_TOKEN));
	}
}
