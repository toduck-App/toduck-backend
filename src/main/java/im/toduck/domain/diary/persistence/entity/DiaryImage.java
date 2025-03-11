package im.toduck.domain.diary.persistence.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diary_images")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE record SET deleted_at = NOW() where id=?")
@SQLRestriction(value = "deleted_at is NULL")
public class DiaryImage extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "diary_id", nullable = false)
	private Diary diary;

	@Column(name = "image_url", length = 256, nullable = false)
	private String imgUrl;

	public DiaryImage(Diary diary, String imgUrl) {
		this.diary = diary;
		this.imgUrl = imgUrl;
	}
}
