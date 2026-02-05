package im.toduck.domain.inquiry.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inquiry")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE inquiry SET deleted_at = NOW() where id=?")
@SQLRestriction(value = "deleted_at is NULL")
public class Inquiry extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Type type;

	@Column(length = 1024, nullable = false)
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	@OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL)
	private List<InquiryImage> inquiryImages = new ArrayList<>();

	@OneToOne(mappedBy = "inquiry", fetch = FetchType.LAZY)
	private InquiryAnswer inquiryAnswer;

	@Builder
	private Inquiry(User user,
		Type type,
		String content,
		Status status) {
		this.user = user;
		this.type = type;
		this.content = content;
		this.status = status;
	}

	public void updateType(Type type) {
		this.type = type;
	}

	public void updateContent(String content) {
		this.content = content;
	}

	public void changeStatus(final Status status) {
		this.status = status;
	}

	public void addAnswer(final InquiryAnswer answer) {
		this.inquiryAnswer = answer;
		answer.setInquiry(this);
	}

	public void removeAnswer() {
		if (this.inquiryAnswer != null) {
			this.inquiryAnswer.setInquiry(null);
			this.inquiryAnswer = null;
		}
	}
}
