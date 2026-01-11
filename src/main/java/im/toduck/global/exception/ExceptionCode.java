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
	NOT_EXIST_PHONE_NUMBER(HttpStatus.BAD_REQUEST, 40121, "가입된 전화번호가 아닙니다.",
		"자체 회원가입에서 ID 찾기 혹은 비밀번호 찾기를 위한 인증번호 요청에서 회원으로 등록되지 않은 전화번호이어서 발생하는 오류입니다."),
	INVALID_LOGIN_ID(HttpStatus.BAD_REQUEST, 40122, "유효하지 않은 아이디입니다.",
		"비밀번호 찾기를 위한 인증번호 요청에서 회원ID와 일치하지 않는 로그인 아이디이어서 발생하는 오류입니다."),

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
	CANNOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, 40206, "자기 자신을 팔로우할 수 없습니다.",
		"사용자가 자신의 계정을 팔로우하려고 시도할 때 발생하는 오류입니다."),
	ALREADY_FOLLOWING(HttpStatus.CONFLICT, 40207, "이미 팔로우 중입니다.",
		"해당 사용자를 이미 팔로우하고 있는 경우 발생하는 오류입니다."),
	NOT_FOUND_FOLLOW(HttpStatus.NOT_FOUND, 40208, "팔로우 정보를 찾을 수 없습니다.",
		"언팔로우 시 팔로우 관계가 존재하지 않을 때 발생하는 오류입니다."),
	EXISTS_USER_NICKNAME(HttpStatus.CONFLICT, 40209, "이미 사용 중인 닉네임입니다."),
	USER_SUSPENDED(HttpStatus.FORBIDDEN, 40210, "계정이 정지되었습니다.", "정지 해제일까지 서비스 이용이 제한됩니다."),
	CANNOT_SUSPEND_SELF(HttpStatus.BAD_REQUEST, 40211, "자기 자신을 정지할 수 없습니다.",
		"관리자는 자신의 계정을 정지할 수 없습니다."),

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
	CANNOT_REPORT_OWN_COMMENT(HttpStatus.FORBIDDEN, 40420, "자신의 댓글은 신고할 수 없습니다."),
	ALREADY_REPORTED_COMMENT(HttpStatus.CONFLICT, 40421, "이미 신고된 댓글입니다."),

	/* 405xx diary */
	NOT_FOUND_DIARY(HttpStatus.NOT_FOUND, 40501, "일기를 찾을 수 없습니다."),
	UNAUTHORIZED_ACCESS_DIARY(HttpStatus.FORBIDDEN, 40502, "일기에 접근 권한이 없습니다."),
	EXISTS_DATE_DIARY(HttpStatus.CONFLICT, 40503, "이미 해당 날짜에 일기가 존재합니다."),

	/* 406xx userKeyword */
	ALREADY_SETUP_KEYWORD(HttpStatus.CONFLICT, 40601, "이미 초기화된 유저입니다."),
	ALREADY_EXISTS_KEYWORD(HttpStatus.CONFLICT, 40602, "이미 존재하는 키워드입니다."),
	NOT_FOUND_KEYWORD(HttpStatus.NOT_FOUND, 40603, "존재하지 않는 키워드입니다."),
	/* 408xx diary_streak */
	NOT_FOUND_DIARY_STREAK(HttpStatus.NOT_FOUND, 40801, "일기 스트릭을 찾을 수 없습니다."),
	/* 409xx diaryKeyword */
	INVALID_KEYWORD_ID(HttpStatus.BAD_REQUEST, 40901, "유효하지 않은 키워드 ID입니다."),

	/* 410xx Events */
	NOT_FOUND_EVENTS(HttpStatus.NOT_FOUND, 41001, "이벤트를 찾을 수 없습니다."),

	/* 411xx EventsDetail */
	NOT_FOUND_EVENTS_DETAIL(HttpStatus.NOT_FOUND, 41101, "이벤트 디테일을 찾을 수 없습니다."),
	DUPLICATE_EVENTS_DETAIL(HttpStatus.CONFLICT, 41102, "해당 이벤트에 대한 이벤트 디테일이 존재합니다."),

	/* 412xx EventsSocial */
	ALREADY_EXISTS_EVENTSSOCIAL(HttpStatus.CONFLICT, 41201, "이미 참여한 유저입니다."),
	UNAUTHORIZED_ACCESS_EVENTSSOCIAL(HttpStatus.FORBIDDEN, 41202, "소셜 이벤트에 접근 권한이 없습니다."),
	NOT_FOUND_EVENTS_SOCIAL(HttpStatus.NOT_FOUND, 41203, "소셜 이벤트 참여 정보를 찾을 수 없습니다."),
	INVALID_CONTENT_LENGTH(HttpStatus.BAD_REQUEST, 41204, "소셜 게시글 글자 수가 이벤트 참여 조건에 부합하지 않습니다."),

	/* 413xx Badge */
	NOT_FOUND_BADGE(HttpStatus.NOT_FOUND, 41301, "뱃지를 찾을 수 없습니다."),

	/* 431xx schedule */
	NOT_FOUND_SCHEDULE_RECORD(HttpStatus.NOT_FOUND, 43101, "일정 기록을 찾을 수 없습니다.",
		"일정 기록을 찾을 수 없을 때 발생하는 오류입니다."),
	NOT_FOUND_SCHEDULE(HttpStatus.NOT_FOUND, 43102, "일정을 찾을 수 없습니다."),
	NON_REPESTITIVE_ONE_SCHEDULE_NOT_PERIOD_DELETE(HttpStatus.BAD_REQUEST, 43103, "반복되지 않는 하루 일정은 기간 삭제가 불가능합니다.",
		"반복되지 않는 하루 일정은 기간 삭제가 불가능한 요청을 클라이언트에서 보냈을 때 발생합니다."),
	ONE_DAY__NONREPEATABLE_SCHEDULE_CANNOT_AFTER_DATE_UPDATE(HttpStatus.FORBIDDEN, 43104,
		"반복되지 않는 하루 일정은 하루 삭제만 가능합니다.",
		"반복되지 않는 하루 일정을 하루 삭제만 가능한 요청을 클라이언트에서 일괄 수정 보냈을 때 발생합니다."),
	PERIOD_SCHEDULE_CANNOT_AFTER_DATE_UPDATE(HttpStatus.BAD_REQUEST, 43105, "기간 일정은 하루 삭제만 가능합니다.",
		"기간 일정을 하루 삭제만 가능한 요청을 클라이언트에서 일괄 수정 보냈을 때 발생합니다."),

	/* 432xx */
	NOT_FOUND_ROUTINE(HttpStatus.NOT_FOUND, 43201, "권한이 없거나 존재하지 않는 루틴입니다."),
	ROUTINE_INVALID_DATE(HttpStatus.BAD_REQUEST, 43202, "유효하지 않은 루틴 날짜입니다.",
		"요청된 날짜에 대한 루틴 변경이 불가능합니다. 루틴의 반복 요일과 현재 날짜를 확인하고 올바른 날짜로 다시 요청해 주세요."),
	PRIVATE_ROUTINE(HttpStatus.FORBIDDEN, 43203, "비공개된 루틴입니다.",
		"요청하신 루틴은 비공개 상태입니다. 접근 권한이 없는 경우 접근할 수 없습니다."),
	EXCEED_ROUTINE_DATE_RANGE(HttpStatus.BAD_REQUEST, 43204, "기간 범위가 유효하지 않거나, 최대 조회 범위를 초과했습니다.",
		"루틴 범위 조회는 14일로 제한됩니다."),

	/* 407xx 백오피스 */
	NOT_FOUND_NOTIFICATION(HttpStatus.NOT_FOUND, 40701, "해당 알림을 찾을 수 없습니다."),
	CANNOT_CANCEL_NOTIFICATION(HttpStatus.CONFLICT, 40702, "취소할 수 없습니다."),
	INVALID_STATISTICS_DATE_RANGE(HttpStatus.BAD_REQUEST, 40703, "통계 조회 날짜 범위가 잘못되었습니다.",
		"시작 날짜가 종료 날짜보다 이후이거나, 최대 조회 기간을 초과했습니다."),
	NOT_FOUND_APP_VERSION(HttpStatus.NOT_FOUND, 40704, "앱 버전을 찾을 수 없습니다."),
	DUPLICATE_APP_VERSION(HttpStatus.CONFLICT, 40705, "이미 존재하는 앱 버전입니다.",
		"해당 플랫폼에 동일한 버전이 이미 등록되어 있습니다."),
	CANNOT_DELETE_APP_VERSION(HttpStatus.CONFLICT, 40706, "삭제할 수 없는 앱 버전입니다.",
		"LATEST, RECOMMENDED, FORCE 정책이 설정된 버전은 삭제할 수 없습니다. 먼저 정책을 NONE으로 변경해주세요."),
	INVALID_UPDATE_POLICY(HttpStatus.BAD_REQUEST, 40707, "유효하지 않은 업데이트 정책입니다.",
		"업데이트 정책 설정이 올바르지 않습니다."),
	DUPLICATE_LATEST_VERSION(HttpStatus.CONFLICT, 40708, "플랫폼당 최신 버전은 하나만 설정할 수 있습니다.",
		"각 플랫폼(iOS, Android)에는 LATEST 정책을 가진 버전이 하나만 존재해야 합니다."),
	INVALID_VERSION_FORMAT(HttpStatus.BAD_REQUEST, 40709, "올바르지 않은 버전 형식입니다.",
		"버전은 x.y.z 형식(예: 1.2.3)이어야 합니다."),

	/* 499xx ETC */
	NOT_FOUND_RESOURCE(HttpStatus.NOT_FOUND, 49901, "해당 경로를 찾을 수 없습니다."),
	METHOD_FORBIDDEN(HttpStatus.METHOD_NOT_ALLOWED, 49902, "지원하지 않는 HTTP 메서드를 사용합니다."),
	INVALID_IMAGE_EXTENSION(HttpStatus.BAD_REQUEST, 49903, "지원되지 않는 이미지 파일 확장자입니다.",
		"이미지 파일 업로드에 허용되지 않는 파일 형식입니다."),
	SMS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 49904, "외부 SMS 통신중 에러가 났습니다.");

	private final HttpStatus httpStatus;
	private final int errorCode;
	private final String message;
	private final String description;

	ExceptionCode(HttpStatus httpStatus, int errorCode, String message) {
		this(httpStatus, errorCode, message, "");
	}
}
