package im.toduck.domain.inquiry.persistence.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import im.toduck.domain.admin.persistence.entity.Admin;
import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inquiry_answer")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE inquiry_answer SET deleted_at = NOW() where id=?")
@SQLRestriction(value = "deleted_at is NULL")
public class InquiryAnswer extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id", nullable = false)
	private Admin admin;

	@Setter
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inquiry_id", nullable = false, unique = true)
	private Inquiry inquiry;

	@Column(length = 1024, nullable = false)
	private String content;

	@Builder
	private InquiryAnswer(Admin admin, Inquiry inquiry, String content) {
		this.admin = admin;
		this.inquiry = inquiry;
		this.content = content;
	}

	public void updateAnswer(final String content, final Admin admin) {
		this.content = content;
		this.admin = admin;
	}

	public void revive(final String content, final Admin admin) {
		this.deletedAt = null;
		this.content = content;
		this.admin = admin;
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}
}
