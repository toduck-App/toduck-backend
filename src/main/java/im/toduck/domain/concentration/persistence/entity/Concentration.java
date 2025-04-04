package im.toduck.domain.concentration.persistence.entity;

import java.time.LocalDate;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import im.toduck.domain.user.persistence.entity.User;
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
@Table(name = "concentration")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE record SET deleted_at = NOW() where id=?")
@SQLRestriction(value = "deleted_at is NULL")
public class Concentration extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "concentration_date", nullable = false)
	private LocalDate date;

	@Column(name = "target_count", nullable = false)
	private Long targetCount = 0L;

	@Column(name = "setting_count", nullable = false)
	private Long settingCount = 0L;

	@Column(name = "concentration_time", nullable = false)
	private Long time = 0L;

	@Builder
	private Concentration(User user, LocalDate date) {
		this.user = user;
		this.date = date;
	}

	public void addTargetCount(long value) {
		this.targetCount += value;
	}

	public void addSettingCount(long value) {
		this.settingCount += value;
	}

	public void addTime(long value) {
		this.time += value;
	}
}
