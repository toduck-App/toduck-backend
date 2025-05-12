package im.toduck.domain.notification.presentation.dto.request;

import im.toduck.domain.notification.persistence.entity.DeviceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeviceTokenRegisterRequest(
	@NotBlank(message = "디바이스 토큰은 필수입니다.")
	@Schema(description = "디바이스 토큰", example = "exampleDeviceToken123456")
	String token,

	@NotNull(message = "디바이스 타입은 필수입니다.")
	@Schema(description = "디바이스 타입", example = "IOS")
	DeviceType deviceType
) {
}
