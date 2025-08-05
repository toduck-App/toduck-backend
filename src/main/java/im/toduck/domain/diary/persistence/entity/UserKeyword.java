package im.toduck.domain.diary.persistence.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_keywords")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE user_keywords SET deleted_at = NOW() where id=?")
@SQLRestriction(value = "deleted_at is NULL")
public class UserKeyword extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private KeywordCategory category;

	@Column(nullable = false, length = 255)
	private String keyword;

	@Column(name = "keyword_count", nullable = false)
	private Long count = 0L;

	@Builder
	private UserKeyword(User user,
		KeywordCategory category,
		String keyword,
		Long count) {
		this.user = user;
		this.category = category;
		this.keyword = keyword;
		this.count = count != null ? count : 0L;
	}

	public void restore(KeywordCategory newCategory) {
		this.deletedAt = null;
		this.category = newCategory;
		this.count = 0L;
	}
}
