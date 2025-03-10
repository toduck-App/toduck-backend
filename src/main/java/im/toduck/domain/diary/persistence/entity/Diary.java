package im.toduck.domain.diary.persistence.entity;

import java.time.LocalDate;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import im.toduck.domain.user.persistence.entity.Emotion;
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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diary")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE record SET deleted_at = NOW() where id=?")
@SQLRestriction(value = "deleted_at is NULL")
public class Diary extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private LocalDate date; // 연월일만 저장

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Emotion emotion;

	@Column(length = 50)
	private String title;

	@Column(length = 256)
	private String img;

	@Column(length = 2048)
	private String memo;
}
