package im.toduck.global.config.swagger;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
	private static final String JWT = "JWT";
	private final Environment environment;
	private final ApiErrorResponseHandler apiErrorResponseHandler;

	@Bean
	public OpenAPI openApi() {
		String activeProfile = "";
		if (!ObjectUtils.isEmpty(environment.getActiveProfiles()) && environment.getActiveProfiles().length >= 1) {
			activeProfile = environment.getActiveProfiles()[0];
		}

		String serverUrl = environment.getProperty("swagger.server-url");
		String serverDescription = environment.getProperty("swagger.description");

		SecurityRequirement securityRequirement = new SecurityRequirement().addList(JWT);

		return new OpenAPI()
			.info(apiInfo(activeProfile))
			.addServersItem(
				new io.swagger.v3.oas.models.servers.Server()
					.url(serverUrl)
					.description(serverDescription)
			)
			.addSecurityItem(securityRequirement)
			.components(securitySchemes());
	}

	@Bean
	ForwardedHeaderFilter forwardedHeaderFilter() {
		return new ForwardedHeaderFilter();
	}

	@Bean
	public OperationCustomizer customize() {
		return (Operation operation, HandlerMethod handlerMethod) -> {
			apiErrorResponseHandler.handleApiErrorResponse(operation, handlerMethod);

			return operation;
		};
	}

	private Components securitySchemes() {
		final SecurityScheme accessTokenSecurityScheme = new SecurityScheme()
			.name(JWT)
			.type(SecurityScheme.Type.HTTP)
			.scheme("Bearer")
			.bearerFormat("JWT")
			.in(SecurityScheme.In.HEADER)
			.name("Authorization");

		return new Components()
			.addSecuritySchemes(JWT, accessTokenSecurityScheme);
	}

	private Info apiInfo(String activeProfile) {
		return new Info()
			.title("Toduck API (" + activeProfile + ")")
			.description("Toduck 백엔드 API 명세서")
			.version("v1.0.0");
	}
}
