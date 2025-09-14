package im.toduck.domain.backoffice.presentation.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import im.toduck.domain.user.persistence.entity.OAuthProvider;
import im.toduck.domain.user.persistence.entity.UserRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDetailResponse {

	private Long id;
	private String nickname;
	private String phoneNumber;
	private String loginId;
	private String email;
	private String imageUrl;
	private UserRole role;
	private OAuthProvider provider;
	private boolean suspended;
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime suspendedUntil;
	private String suspensionReason;
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime createdAt;
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime updatedAt;

	@Builder
	private UserDetailResponse(Long id, String nickname, String phoneNumber, String loginId,
			String email, String imageUrl, UserRole role, OAuthProvider provider,
			boolean suspended, LocalDateTime suspendedUntil, String suspensionReason,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
		this.loginId = loginId;
		this.email = email;
		this.imageUrl = imageUrl;
		this.role = role;
		this.provider = provider;
		this.suspended = suspended;
		this.suspendedUntil = suspendedUntil;
		this.suspensionReason = suspensionReason;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}
