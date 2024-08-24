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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "social_image_file")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialImageFile extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "social_id", nullable = false)
	private Social social;

	@Column(nullable = false, length = 1024)
	private String url;

	private SocialImageFile(Social social, String url) {
		this.social = social;
		this.url = url;
	}

	public static SocialImageFile of(Social social, String url) {
		return new SocialImageFile(social, url);
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}
}
