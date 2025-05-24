package im.toduck.domain.social.persistence.entity;

import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "social_report")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialReport extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "social_id", nullable = false)
	private Social social;

	@Enumerated(EnumType.STRING)
	@Column(name = "report_type", nullable = false)
	private ReportType reportType;

	private String reason;

	@Builder
	public SocialReport(User user, Social social, ReportType reportType, String reason) {
		this.user = user;
		this.social = social;
		this.reportType = reportType;
		this.reason = reason;
	}
}
