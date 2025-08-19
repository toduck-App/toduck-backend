package im.toduck.domain.diary.persistence.entity;

import java.time.LocalDate;

import im.toduck.domain.user.persistence.entity.User;
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
@Table(name = "diary_streak")
@Getter
@NoArgsConstructor
public class DiaryStreak {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "streak", nullable = false)
	private Long streak = 0L;

	@Column(name = "last_diary_date", nullable = true)
	private LocalDate lastDiaryDate;

	@Builder
	private DiaryStreak(
		User user,
		Long streak,
		LocalDate lastDiaryDate
	) {
		this.user = user;
		this.streak = streak;
		this.lastDiaryDate = lastDiaryDate;
	}

	public void updateStreak(Long streak) {
		this.streak = streak;
	}

	public void updateLastDiaryDate(LocalDate lastDiaryDate) {
		this.lastDiaryDate = lastDiaryDate;
	}
}
