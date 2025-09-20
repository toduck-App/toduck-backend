package im.toduck.domain.backoffice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import im.toduck.domain.user.persistence.entity.OAuthProvider;
import im.toduck.domain.user.persistence.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "백오피스 회원 목록 페이지네이션 응답 DTO")
public class UserListPaginationResponse {

	@Schema(description = "회원 목록")
	private List<UserInfo> users;

	@Schema(description = "페이지네이션 정보")
	private PageInfo pageInfo;

	@Builder
	private UserListPaginationResponse(List<UserInfo> users, PageInfo pageInfo) {
		this.users = users;
		this.pageInfo = pageInfo;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@Schema(description = "회원 정보")
	public static class UserInfo {
		@Schema(description = "회원 ID", example = "1")
		private Long id;

		@Schema(description = "닉네임", example = "홍길동")
		private String nickname;

		@Schema(description = "전화번호 (일반 회원만, 소셜 로그인 회원은 null)", example = "010-1234-5678")
		private String phoneNumber;

		@Schema(description = "로그인 ID (일반 회원만, 소셜 로그인 회원은 null)", example = "hong123")
		private String loginId;

		@Schema(description = "이메일 (소셜 로그인 회원만, 일반 회원은 null)", example = "hong@gmail.com")
		private String email;

		@Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
		private String imageUrl;

		@Schema(description = "회원 역할", example = "USER")
		private UserRole role;

		@Schema(description = "소셜 로그인 제공자 (KAKAO, APPLE만 사용, 일반 회원은 null)", example = "KAKAO")
		private OAuthProvider provider;

		@Schema(description = "정지 여부 (현재 시점 기준)", example = "false")
		private boolean suspended;

		@JsonSerialize(using = LocalDateTimeSerializer.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
		@Schema(description = "정지 해제일 (현재 정지 상태인 경우에만 제공, 아니면 null)", example = "2024-12-31 23:59")
		private LocalDateTime suspendedUntil;

		@Schema(description = "정지 사유 (현재 정지 상태인 경우에만 제공, 아니면 null)", example = "부적절한 게시물 작성")
		private String suspensionReason;

		@JsonSerialize(using = LocalDateTimeSerializer.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
		@Schema(description = "가입일", example = "2024-01-01 00:00")
		private LocalDateTime createdAt;

		@JsonSerialize(using = LocalDateTimeSerializer.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
		@Schema(description = "마지막 수정일", example = "2024-01-15 12:30")
		private LocalDateTime updatedAt;

		@Builder
		private UserInfo(Long id, String nickname, String phoneNumber, String loginId, String email,
				String imageUrl, UserRole role, OAuthProvider provider, boolean suspended,
				LocalDateTime suspendedUntil, String suspensionReason, LocalDateTime createdAt,
				LocalDateTime updatedAt) {
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

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@Schema(description = "페이지네이션 정보")
	public static class PageInfo {
		@Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
		private int currentPage;

		@Schema(description = "페이지 크기", example = "20")
		private int pageSize;

		@Schema(description = "전체 페이지 수", example = "5")
		private int totalPages;

		@Schema(description = "전체 회원 수", example = "100")
		private long totalElements;

		@Schema(description = "첫 번째 페이지 여부", example = "true")
		private boolean first;

		@Schema(description = "마지막 페이지 여부", example = "false")
		private boolean last;

		@Schema(description = "다음 페이지 존재 여부", example = "true")
		private boolean hasNext;

		@Schema(description = "이전 페이지 존재 여부", example = "false")
		private boolean hasPrevious;

		@Builder
		private PageInfo(int currentPage, int pageSize, int totalPages, long totalElements,
				boolean first, boolean last, boolean hasNext, boolean hasPrevious) {
			this.currentPage = currentPage;
			this.pageSize = pageSize;
			this.totalPages = totalPages;
			this.totalElements = totalElements;
			this.first = first;
			this.last = last;
			this.hasNext = hasNext;
			this.hasPrevious = hasPrevious;
		}
	}
}
