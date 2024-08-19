package im.toduck.domain.social.persistence.entity;

import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "social_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialCategory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 20)
	private String name;

	private SocialCategory(String name) {
		this.name = name;
	}

	public static SocialCategory from(String name) {
		return new SocialCategory(name);
	}
}
