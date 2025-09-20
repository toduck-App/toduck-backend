package im.toduck.domain.backoffice.persistence.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "앱 플랫폼")
public enum AppVersionPlatform {
	@Schema(description = "iOS")
	IOS,

	@Schema(description = "Android")
	ANDROID
}
