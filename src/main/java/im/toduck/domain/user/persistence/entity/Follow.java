package im.toduck.domain.user.persistence.entity;

import im.toduck.global.base.entity.BaseEntity;
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
@Table(name = "follow")
@NoArgsConstructor
public class Follow extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "followed_id", nullable = false)
	private User followed;

	@ManyToOne
	@JoinColumn(name = "follower_id", nullable = false)
	private User follower;
}
