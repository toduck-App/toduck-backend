package im.toduck.domain.backoffice.persistence.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "업데이트 타입")
public enum UpdateType {
	@Schema(description = "최신 버전")
	LATEST("최신 버전"),

	@Schema(description = "권장 업데이트")
	RECOMMENDED("권장 업데이트"),

	@Schema(description = "강제 업데이트")
	FORCE("강제 업데이트"),

	@Schema(description = "업데이트 정책 없음")
	NONE("업데이트 정책 없음");

	private final String description;

	UpdateType(final String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}
}
