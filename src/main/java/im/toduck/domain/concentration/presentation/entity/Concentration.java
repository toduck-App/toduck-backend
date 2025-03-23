package im.toduck.domain.concentration.presentation.entity;

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
	private LocalDate concentrationDate;

	@Column(name = "target_count", nullable = false)
	private Integer targetCount = 0;

	@Column(name = "setting_count", nullable = false)
	private Integer settingCount = 0;

	@Column(nullable = false)
	private Integer time = 0;

	public void addTargetCount(int value) {
		this.targetCount += value;
	}

	public void addSettingCount(int value) {
		this.settingCount += value;
	}

	public void addTime(int value) {
		this.time += value;
	}
}
