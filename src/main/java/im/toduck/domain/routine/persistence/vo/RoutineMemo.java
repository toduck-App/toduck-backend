package im.toduck.domain.routine.persistence.vo;

import im.toduck.global.exception.VoException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RoutineMemo {
	private static final int MAX_LENGTH = 40;

	@Column(name = "memo", columnDefinition = "TEXT")
	private String value;

	private RoutineMemo(String value) {
		validate(value);
		this.value = value;
	}

	public static RoutineMemo from(String memo) {
		if (memo == null) {
			return null;
		}
		return new RoutineMemo(memo);
	}

	private void validate(String value) {
		if (value != null && value.length() > MAX_LENGTH) {
			throw new VoException("메모는 " + MAX_LENGTH + "자를 초과할 수 없습니다.");
		}
	}
}
