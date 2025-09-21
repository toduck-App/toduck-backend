package im.toduck.domain.events.social.persistence.entity;

import java.time.LocalDate;

import im.toduck.domain.social.persistence.entity.Social;
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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "events_social")
@NoArgsConstructor
public class EventsSocial {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "social_id", nullable = false)
	private Social social;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(length = 31, nullable = false)
	private String phone;

	@Column(name = "participation_date", nullable = false)
	private LocalDate date;

	@Builder
	private EventsSocial(Social social, User user, String phone, LocalDate date) {
		this.social = social;
		this.user = user;
		this.phone = phone;
		this.date = date;
	}
}
