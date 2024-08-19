package im.toduck.domain.social.persistence.entity;

import java.time.LocalDateTime;

import im.toduck.global.base.entity.BaseEntity;
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
@Table(name = "social_category_link")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialCategoryLink extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "social_id", nullable = false)
	private Social social;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "social_category_id", nullable = false)
	private SocialCategory socialCategory;

	private SocialCategoryLink(Social social, SocialCategory socialCategory) {
		this.social = social;
		this.socialCategory = socialCategory;
	}

	public static SocialCategoryLink of(Social social, SocialCategory socialCategory) {
		return new SocialCategoryLink(social, socialCategory);
	}

	public void remove() {
		this.deletedAt = LocalDateTime.now();
	}
}
