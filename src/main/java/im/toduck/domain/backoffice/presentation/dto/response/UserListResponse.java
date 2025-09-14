package im.toduck.domain.backoffice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import im.toduck.domain.user.persistence.entity.UserRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserListResponse {

	private List<UserInfo> users;
	private long totalCount;

	@Builder
	private UserListResponse(List<UserInfo> users, long totalCount) {
		this.users = users;
		this.totalCount = totalCount;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class UserInfo {
		private Long id;
		private String nickname;
		private String phoneNumber;
		private String email;
		private UserRole role;
		private boolean suspended;
		@JsonSerialize(using = LocalDateTimeSerializer.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
		private LocalDateTime suspendedUntil;
		private String suspensionReason;
		@JsonSerialize(using = LocalDateTimeSerializer.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
		private LocalDateTime createdAt;

		@Builder
		private UserInfo(Long id, String nickname, String phoneNumber, String email,
				UserRole role, boolean suspended, LocalDateTime suspendedUntil, String suspensionReason,
				LocalDateTime createdAt) {
			this.id = id;
			this.nickname = nickname;
			this.phoneNumber = phoneNumber;
			this.email = email;
			this.role = role;
			this.suspended = suspended;
			this.suspendedUntil = suspendedUntil;
			this.suspensionReason = suspensionReason;
			this.createdAt = createdAt;
		}
	}
}
