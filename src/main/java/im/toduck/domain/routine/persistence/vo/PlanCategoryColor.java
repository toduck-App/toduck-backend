package im.toduck.domain.routine.persistence.vo;

import static im.toduck.global.regex.PlanRegex.*;

import im.toduck.global.exception.VoException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PlanCategoryColor {

	@Column(name = "color")
	private String value;

	private PlanCategoryColor(String value) {
		validate(value);
		this.value = value;
	}

	public static PlanCategoryColor from(String color) {
		if (color == null) {
			return null;
		}
		return new PlanCategoryColor(color);
	}

	private void validate(String value) {
		if (value != null) {
			if (!value.matches(HEX_COLOR_CODE_REGEX)) {
				throw new VoException("색상은 '#RRGGBB' 형식이어야 합니다.");
			}
		}
	}
}
