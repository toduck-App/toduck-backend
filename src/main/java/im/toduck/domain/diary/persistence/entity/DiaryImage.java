package im.toduck.domain.diary.persistence.entity;

import java.time.LocalDateTime;

import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "diary_image_file")
@Getter
@NoArgsConstructor
public class DiaryImage extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "diary_id", nullable = false)
	private Diary diary;

	@Column(name = "url", length = 512, nullable = false)
	private String url;

	@Builder
	private DiaryImage(Diary diary, String url) {
		this.diary = diary;
		this.url = url;
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}
}
