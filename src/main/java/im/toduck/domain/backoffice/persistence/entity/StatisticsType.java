package im.toduck.domain.backoffice.persistence.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "통계 타입")
public enum StatisticsType {
	@Schema(description = "신규 회원 가입")
	NEW_USERS,

	@Schema(description = "탈퇴 회원")
	DELETED_USERS,

	@Schema(description = "생성된 루틴")
	NEW_ROUTINES,

	@Schema(description = "생성된 일기")
	NEW_DIARIES,

	@Schema(description = "신규 소셜 게시물")
	NEW_SOCIAL_POSTS,

	@Schema(description = "신규 댓글")
	NEW_COMMENTS,

	@Schema(description = "신규 스케줄")
	NEW_SCHEDULES
}
