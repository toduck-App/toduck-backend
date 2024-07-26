package im.toduck.domain.persistence.entity.person;

import java.time.LocalDate;
import java.time.LocalTime;

import im.toduck.domain.persistence.entity.user.User;
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
@Table(name = "schedule")
@NoArgsConstructor
public class Schedule extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Emoji emoji;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color color;

	@Column(nullable = false)
	private Boolean isComplete;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 255)
	private String location;

	@Column(nullable = false) //TODO : 루틴 날짜 로직에 따라 null
	private LocalDate date;

	@Column(nullable = false)
	private LocalTime time;

	@Column(nullable = false)
	private Alarm alarm;

	@Column(nullable = false, length = 255)
	private String memo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
}
