package im.toduck.domain.diary.persistence.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import im.toduck.domain.user.persistence.entity.Emotion;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diary")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE diary SET deleted_at = NOW() where id=?")
@SQLRestriction(value = "deleted_at is NULL")
public class Diary extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "diary_date", nullable = false)
	private LocalDate date;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Emotion emotion;

	@Column(length = 50)
	private String title;

	@Lob
	private String memo;

	@OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
	private List<DiaryImage> diaryImages = new ArrayList<>();

	@OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DiaryKeyword> diaryKeywords = new ArrayList<>();

	@Builder
	private Diary(User user,
		LocalDate date,
		Emotion emotion,
		String title,
		String memo) {
		this.user = user;
		this.date = date;
		this.emotion = emotion;
		this.title = title;
		this.memo = memo;
	}

	public boolean isOwner(User requestingUser) {
		return this.user.getId().equals(requestingUser.getId());
	}

	public void updateEmotion(Emotion emotion) {
		this.emotion = emotion;
	}

	public void updateTitle(String title) {
		this.title = title;
	}

	public void updateMemo(String memo) {
		this.memo = memo;
	}
}
