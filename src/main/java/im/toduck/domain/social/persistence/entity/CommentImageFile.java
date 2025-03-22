package im.toduck.domain.social.persistence.entity;

import java.time.LocalDateTime;

import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment_image_file")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentImageFile extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id", nullable = false, unique = true)
	private Comment comment;

	@Column(nullable = false, length = 1024)
	private String url;

	@Builder
	private CommentImageFile(final Comment comment, final String url) {
		this.comment = comment;
		this.url = url;
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}
}
