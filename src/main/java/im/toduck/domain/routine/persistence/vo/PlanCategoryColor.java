package im.toduck.domain.routine.persistence.vo;

import static im.toduck.global.regex.PlanRegex.*;

import java.util.regex.Pattern;

import im.toduck.global.exception.VoException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public class PlanCategoryColor {
	private static final Pattern HEX_COLOR_CODE_PATTERN = Pattern.compile(HEX_COLOR_CODE_REGEX);

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
			if (!HEX_COLOR_CODE_PATTERN.matcher(value).matches()) {
				throw new VoException("색상은 '#RRGGBB' 형식이어야 합니다.");
			}
		}
	}
}
