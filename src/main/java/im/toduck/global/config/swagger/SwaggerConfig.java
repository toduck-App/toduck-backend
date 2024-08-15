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
	private final ApiSuccessResponseHandler apiSuccessResponseHandler;
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
			apiSuccessResponseHandler.handleApiSuccessResponse(operation, handlerMethod);
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
			.title("Toduck 백엔드 API 명세서 (" + activeProfile + ")")
			.description(
				"<p>이 문서는 Toduck 백엔드 API의 사용 방법과 예시를 제공합니다. "
					+
					"API에 대한 보다 자세한 설명은 <a href=\"https://kyxxn.notion.site/API-e775e161efa6459583a0ee0d586c4d19?pvs=74\" target='_blank'>API 개요</a>를 참고해 주세요.</p>"
					+
					"<p>사용 중 발생할 수 있는 예외 코드 목록은 <a href=\"/exception-codes\" target='_blank'>여기</a>에서 확인할 수 있습니다. 예외 응답은 Client에 노출 가능한 한정된 정보만 제공됩니다.</p>"
					+
					"<p>API 사용 중 문제가 발생하거나 추가 문의가 필요한 경우, 담당자에게 문의해 주세요.</p>"
					+
					"<p>/auth 로 시작하는 엔드포인터에는 Authorization Header 를 포함하지 않아야 합니다. 포함하는 경우 40102 에러가 발생합니다.</p>"
			)
			.version("v1.0.0");
	}
}
