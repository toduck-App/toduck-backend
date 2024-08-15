package im.toduck.domain.social.persistence.entity;

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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "social_image_file")
@NoArgsConstructor
public class SocialImageFile extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 1024)
	private String url;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "social_id", nullable = false)
	private Social social;
}
