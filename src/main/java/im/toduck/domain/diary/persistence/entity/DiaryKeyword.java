package im.toduck.domain.diary.persistence.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diary_keywords")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE diary SET deleted_at = NOW() where id=?")
@SQLRestriction(value = "deleted_at is NULL")
public class DiaryKeyword extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "diary_id", nullable = false)
	private Diary diary;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_keyword_id", nullable = false)
	private UserKeyword userKeyword;

	@Column(nullable = false)
	private boolean checked = false;

	@Builder
	private DiaryKeyword(Diary diary, UserKeyword userKeyword, boolean checked) {
		this.diary = diary;
		this.userKeyword = userKeyword;
		this.checked = checked;
	}
}
