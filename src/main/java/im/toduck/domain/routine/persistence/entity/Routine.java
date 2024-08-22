package im.toduck.domain.routine.persistence.entity;

import java.time.LocalTime;

import im.toduck.domain.person.Color;
import im.toduck.domain.person.Emoji;
import im.toduck.domain.social.Social;
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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "routine")
@NoArgsConstructor
public class Routine extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// TODO: 변경 필요
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Emoji emoji;

	// TODO: 변경 필요
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color color;

	@Column(nullable = false, columnDefinition = "CHAR(100)")
	private String title;

	@Column(nullable = false)
	private Boolean isPublic;

	@Column(name = "reminder_minutes")
	private Integer reminderMinutes;

	@Column(columnDefinition = "TEXT")
	private String memo;

	@Column
	private LocalTime time;

	@Column(name = "days_of_week", nullable = false)
	private Byte daysOfWeek;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "social_id", nullable = false)
	private Social social;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
}
