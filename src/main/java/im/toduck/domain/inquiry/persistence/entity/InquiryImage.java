package im.toduck.domain.inquiry.persistence.entity;

import java.time.LocalDateTime;

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
@Table(name = "inquiry_image_file")
@Getter
@NoArgsConstructor
public class InquiryImage extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inquiry_id", nullable = false)
	private Inquiry inquiry;

	@Column(name = "url", length = 1024, nullable = false)
	private String url;

	@Builder
	private InquiryImage(Inquiry inquiry, String url) {
		this.inquiry = inquiry;
		this.url = url;
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}
}
