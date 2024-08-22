package im.toduck.domain.social.persistence.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "social")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Social extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// TODO: Routine 추가 (생성자, 정적 팩터리 메소드에도 추가 필요)

	@Column(nullable = false, length = 255)
	private String content;

	@Column(nullable = false)
	private Boolean isAnonymous;

	@OneToMany(mappedBy = "social", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SocialCategoryLink> socialCategoryLinkList = new ArrayList<>();

	@OneToMany(mappedBy = "social", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SocialImageFile> socialImageFileList = new ArrayList<>();

	private Social(User user, String content, Boolean isAnonymous) {
		this.user = user;
		this.content = content;
		this.isAnonymous = isAnonymous;
	}

	public static Social of(User user, String content, Boolean isAnonymous) {
		return new Social(user, content, isAnonymous);
	}

	public void remove() {
		for (SocialCategoryLink socialCategoryLink : socialCategoryLinkList) {
			socialCategoryLink.remove();
		}
		for (SocialImageFile socialImageFile : socialImageFileList) {
			socialImageFile.remove();
		}
		this.deletedAt = LocalDateTime.now();
	}

	public void addSocialImageFiles(List<String> socialImageUrls) {
		for (String imageUrl : socialImageUrls) {
			socialImageFileList.add(SocialImageFile.of(this, imageUrl));
		}
	}

	public void addSocialCategoryLinks(List<SocialCategory> socialCategories) {
		for (SocialCategory socialCategory : socialCategories) {
			socialCategoryLinkList.add(SocialCategoryLink.of(this, socialCategory));
		}
	}

	public boolean isOwner(User requestingUser) {
		return this.user.getId().equals(requestingUser.getId());
	}
}
