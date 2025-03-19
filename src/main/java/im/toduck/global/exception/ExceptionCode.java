package im.toduck.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 40000번대 예외 코드를 관리하는 열거형 클래스입니다.
 *
 * <p>이 클래스는 40000번대의 애플리케이션 특정 오류 코드만 관리합니다.
 * 다른 범위의 예외 코드(30000번대, 50000번대)는 {@link im.toduck.global.handler.GlobalExceptionHandler}
 * 와 {@link im.toduck.global.presentation.ApiResponse}에서 예외 상황에 맞게 일괄적으로 관리됩니다.</p>
 *
 * <ul>
 * <li>{@link HttpStatus} httpStatus - HTTP 상태 코드</li>
 * <li>int errorCode - 애플리케이션 특정 오류 코드 (40000번대)</li>
 * <li>String message - 사용자 친화적인 오류 메시지</li>
 * <li>String description - 추가 오류 정보</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

	/* 401xx AUTH */
	INVALID_LOGIN_ID_OR_PASSWORD(HttpStatus.UNAUTHORIZED, 40101, "아이디 또는 비밀번호가 일치하지 않습니다.",
		"사용자가 제공한 전화번호나 비밀번호가 데이터베이스의 정보와 일치하지 않을 때 발생합니다."),
	FORBIDDEN_ACCESS_TOKEN(HttpStatus.FORBIDDEN, 40102, "토큰에 접근 권한이 없습니다."),
	EMPTY_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, 40103, "토큰이 포함되어 있지 않습니다."),
	EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, 40104, "재 로그인이 필요합니다.",
		"해당 애러 발생시, RefreshToken을 통해 AccessToken을 재발급 해주세요. 해당 오류는 권한이 필요한 모든 엔드포인트에서 발생할 수 있습니다."),
	MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, 40105, "비정상적인 토큰입니다.", "보안 위험이므로 완전히 로그아웃 처리해주세요."),
	TAMPERED_TOKEN(HttpStatus.UNAUTHORIZED, 40106, "서명이 조작된 토큰입니다.", "보안 위험이므로 완전히 로그아웃 처리해주세요."),
	UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, 40107, "지원하지 않는 토큰입니다.", "보안 위험이므로 완전히 로그아웃 처리해주세요."),
	TAKEN_AWAY_TOKEN(HttpStatus.FORBIDDEN, 40108, "인증 불가, 관리자에게 문의하세요.", "보안 위험이므로 완전히 로그아웃 처리해주세요."),
	EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 40109, "재 로그인이 필요합니다.",
		"해당 예외 발생시, RefreshToken까지 만료된 경우이므로, 재 로그인을 수행해 주세요."),
	EXISTS_PHONE_NUMBER(HttpStatus.CONFLICT, 40110, "이미 가입된 전화번호입니다."),
	OVER_MAX_MESSAGE_COUNT(HttpStatus.FORBIDDEN, 40111, "인증코드 요청 횟수를 초과하였습니다."),
	OVER_MAX_VERIFIED_COUNT(HttpStatus.FORBIDDEN, 40112, "인증코드 확인 횟수를 초과하였습니다."),
	EXISTS_USER_ID(HttpStatus.CONFLICT, 40113, "이미 가입된 아이디입니다."),
	NOT_SEND_PHONE_NUMBER(HttpStatus.NOT_FOUND, 40114, "인증 요청이 보내지 않은 전화번호입니다."),
	INVALID_VERIFIED_CODE(HttpStatus.FORBIDDEN, 40115, "인증 코드가 일치하지 않습니다."),
	NOT_VERIFIED_PHONE_NUMBER(HttpStatus.FORBIDDEN, 40116, "인증되지 않은 전화번호입니다."),
	EXISTS_EMAIL(HttpStatus.CONFLICT, 40117, "이미 가입된 이메일입니다."),
	INVALID_ID_TOKEN(HttpStatus.FORBIDDEN, 40118, "유효하지 않은 ID 토큰입니다.", "ID 토큰이 유효하지 않을 때 발생하는 오류입니다."),
	ABNORMAL_ID_TOKEN(HttpStatus.FORBIDDEN, 40119, "비정상적인 ID 토큰입니다.", "ID 토큰 공개키로 암호화 도중에 발생하는 오류입니다."),
	NOT_MATCHED_PUBLIC_KEY(HttpStatus.NOT_FOUND, 40120, "일치하는 공개키를 찾을 수 없습니다.", "KID 와 공개키가 일치하지 않을 때 발생하는 오류입니다."),

	/* 402xx */
	NOT_FOUND_USER(HttpStatus.NOT_FOUND, 40201, "사용자를 찾을 수 없습니다."),
	INVALID_USER_FILED(HttpStatus.FORBIDDEN, 40202, "유효하지 않은 사용자 필드입니다.",
		"Oauth 사용자 필드와 일반 사용자 필드가 중복되어 사용될 때 발생하는 오류입니다."),
	CANNOT_BLOCK_SELF(HttpStatus.BAD_REQUEST, 40203, "자기 자신을 차단할 수 없습니다.",
		"사용자가 자신의 계정을 차단하려고 시도할 때 발생하는 오류입니다."),
	NOT_FOUND_BLOCK(HttpStatus.NOT_FOUND, 40204, "차단 정보를 찾을 수 없습니다.",
		"차단 해제 시 차단 정보를 찾을 수 없을 때 발생하는 오류입니다."),
	ALREADY_BLOCKED(HttpStatus.CONFLICT, 40205, "이미 차단된 사용자입니다.",
		"해당 사용자를 이미 차단한 경우 발생하는 오류입니다."),

	/* 404xx */
	NOT_FOUND_SOCIAL_BOARD(HttpStatus.NOT_FOUND, 40401, "게시글을 찾을 수 없습니다."),
	UNAUTHORIZED_ACCESS_SOCIAL_BOARD(HttpStatus.FORBIDDEN, 40402, "게시글에 접근 권한이 없습니다."),
	NOT_FOUND_SOCIAL_CATEGORY(HttpStatus.NOT_FOUND, 40403, "찾을 수 없는 카테고리가 포함되어 있습니다."),
	NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, 40404, "해당 댓글을 찾을 수 없습니다."),
	UNAUTHORIZED_ACCESS_COMMENT(HttpStatus.FORBIDDEN, 40405, "해당 댓글에 접근 권한이 없습니다."),
	INVALID_COMMENT_FOR_BOARD(HttpStatus.BAD_REQUEST, 40406, "해당 게시글에 댓글이 속하지 않습니다."),
	EXISTS_LIKE(HttpStatus.CONFLICT, 40407, "이미 좋아요가 존재합니다."),
	NOT_FOUND_LIKE(HttpStatus.NOT_FOUND, 40408, "해당 좋아요를 찾을 수 없습니다."),
	UNAUTHORIZED_ACCESS_LIKE(HttpStatus.FORBIDDEN, 40409, "해당 좋아요에 접근 권한이 없습니다."),
	INVALID_LIKE_FOR_BOARD(HttpStatus.BAD_REQUEST, 40410, "해당 게시글에 좋아요가 속하지 않습니다."),
	EMPTY_SOCIAL_CATEGORY_LIST(HttpStatus.BAD_REQUEST, 40411, "카테고리 목록은 최소 1개의 항목을 포함해야 합니다."),
	BLOCKED_USER_SOCIAL_ACCESS(HttpStatus.BAD_REQUEST, 40412, "차단된 사용자의 게시글에 접근할 수 없습니다."),
	ALREADY_REPORTED(HttpStatus.CONFLICT, 40413, "이미 신고된 게시글입니다.",
		"이미 신고한 게시글에 대해 다시 신고를 시도할 때 발생하는 오류입니다."),
	CANNOT_REPORT_OWN_POST(HttpStatus.FORBIDDEN, 40414, "자신의 게시글은 신고할 수 없습니다."),
	EXISTS_COMMENT_LIKE(HttpStatus.CONFLICT, 40415, "이미 댓글에 좋아요를 눌렀습니다."),
	NOT_FOUND_COMMENT_LIKE(HttpStatus.NOT_FOUND, 40416, "해당 댓글 좋아요를 찾을 수 없습니다."),
	INVALID_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, 40417, "검색 키워드는 null일 수 없습니다."),
	NOT_FOUND_PARENT_COMMENT(HttpStatus.NOT_FOUND, 40418, "부모 댓글을 찾을 수 없습니다."),
	INVALID_PARENT_COMMENT(HttpStatus.BAD_REQUEST, 40419, "답글은 부모 댓글이 될 수 없습니다."),

	/* 405xx diary */

	/* 411xx schedule */
	NOT_FOUND_SCHEDULE_RECORD(HttpStatus.NOT_FOUND, 41101, "일정 기록을 찾을 수 없습니다.",
		"일정 기록을 찾을 수 없을 때 발생하는 오류입니다."),
	NOT_FOUND_SCHEDULE(HttpStatus.NOT_FOUND, 41102, "일정을 찾을 수 없습니다."),
	NON_REPESTITIVE_ONE_SCHEDULE_NOT_PERIOD_DELETE(HttpStatus.BAD_REQUEST, 41103, "반복되지 않는 하루 일정은 기간 삭제가 불가능합니다.",
		"반복되지 않는 하루 일정은 기간 삭제가 불가능한 요청을 클라이언트에서 보냈을 때 발생합니다."),

	/* 432xx */
	NOT_FOUND_ROUTINE(HttpStatus.NOT_FOUND, 43201, "권한이 없거나 존재하지 않는 루틴입니다."),
	ROUTINE_INVALID_DATE(HttpStatus.BAD_REQUEST, 43202, "유효하지 않은 루틴 날짜입니다.",
		"요청된 날짜에 대한 루틴 변경이 불가능합니다. 루틴의 반복 요일과 현재 날짜를 확인하고 올바른 날짜로 다시 요청해 주세요."),
	PRIVATE_ROUTINE(HttpStatus.FORBIDDEN, 43203, "비공개된 루틴입니다.",
		"요청하신 루틴은 비공개 상태입니다. 접근 권한이 없는 경우 접근할 수 없습니다."),

	/* 499xx ETC */
	NOT_FOUND_RESOURCE(HttpStatus.NOT_FOUND, 49901, "해당 경로를 찾을 수 없습니다."),
	METHOD_FORBIDDEN(HttpStatus.METHOD_NOT_ALLOWED, 49902, "지원하지 않는 HTTP 메서드를 사용합니다."),
	INVALID_IMAGE_EXTENSION(HttpStatus.BAD_REQUEST, 49903, "지원되지 않는 이미지 파일 확장자입니다.",
		"이미지 파일 업로드에 허용되지 않는 파일 형식입니다.");

	private final HttpStatus httpStatus;
	private final int errorCode;
	private final String message;
	private final String description;

	ExceptionCode(HttpStatus httpStatus, int errorCode, String message) {
		this(httpStatus, errorCode, message, "");
	}
}
