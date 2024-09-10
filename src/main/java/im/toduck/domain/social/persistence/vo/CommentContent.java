package im.toduck.domain.social.persistence.vo;

import im.toduck.global.exception.VoException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentContent {

	@Column(name = "content")
	private String value;

	private CommentContent(String value) {
		validate(value);
		this.value = value;
	}

	public static CommentContent from(String content) {
		if (content == null) {
			return null;
		}
		return new CommentContent(content);
	}

	private void validate(String value) {
		if (value == null || value.trim().isEmpty()) {
			throw new VoException("댓글 내용은 비어 있을 수 없습니다.");
		}
	}
}
