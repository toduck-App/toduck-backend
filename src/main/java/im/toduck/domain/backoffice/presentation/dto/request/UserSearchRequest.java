package im.toduck.domain.backoffice.presentation.dto.request;

import im.toduck.domain.user.persistence.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "백오피스 회원 검색 요청 DTO")
public record UserSearchRequest(
	@Schema(description = "검색 키워드", example = "홍길동")
	String keyword,

	@Schema(
		description = "검색 필드 유형 (nickname: 닉네임, email: 이메일, phone: 전화번호, loginid: 로그인 ID, null: 전체 필드 검색)",
		example = "nickname",
		allowableValues = {"nickname", "email", "phone", "loginid"}
	)
	String searchType,

	@Schema(
		description = "회원 상태 필터 (all: 전체, active: 활성, suspended: 정지)",
		example = "all",
		allowableValues = {"all", "active", "suspended"}
	)
	String status,

	@Schema(description = "회원 역할 필터", example = "USER")
	UserRole role,

	@Schema(description = "회원 유형 필터 (GENERAL: 일반회원, KAKAO: 카카오 로그인, APPLE: 애플 로그인)", example = "KAKAO")
	String provider,

	@Schema(
		description = "정렬 기준 (createdAt: 생성일, nickname: 닉네임, email: 이메일, role: 역할, suspendedAt: 정지일, updatedAt: 수정일)",
		example = "createdAt",
		allowableValues = {"createdAt", "nickname", "email", "role", "suspendedAt", "updatedAt"}
	)
	String sortBy,

	@Schema(
		description = "정렬 방향 (asc: 오름차순, desc: 내림차순)",
		example = "desc",
		allowableValues = {"asc", "desc"}
	)
	String sortDirection,

	@Schema(description = "페이지 번호 (0부터 시작)", example = "0")
	Integer page,

	@Schema(description = "페이지 크기 (기본값: 20, 최대: 100)", example = "20")
	Integer size
) {
	public UserSearchRequest {
		if (page == null || page < 0) {
			page = 0;
		}
		if (size == null || size <= 0 || size > 100) {
			size = 20;
		}
		if (sortBy == null || sortBy.trim().isEmpty()) {
			sortBy = "createdAt";
		}
		if (sortDirection == null || sortDirection.trim().isEmpty()) {
			sortDirection = "desc";
		}
		if (status == null || status.trim().isEmpty()) {
			status = "all";
		}
	}
}
