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
 * </ul>
 */
@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

	/* 401xx AUTH */
	INVALID_PHONE_NUMBER_OR_PASSWORD(HttpStatus.UNAUTHORIZED, 40101, "전화번호 또는 비밀번호가 일치하지 않습니다."),
	FORBIDDEN_ACCESS_TOKEN(HttpStatus.FORBIDDEN, 40102, "토큰에 접근 권한이 없습니다."),
	EMPTY_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, 40103, "토큰이 포함되어 있지 않습니다."),
	EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, 40104, "재 로그인이 필요합니다."),
	MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, 40105, "비정상적인 토큰입니다."),
	TAMPERED_TOKEN(HttpStatus.UNAUTHORIZED, 40106, "서명이 조작된 토큰입니다."),
	UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, 40107, "지원하지 않는 토큰입니다."),
	TAKEN_AWAY_TOKEN(HttpStatus.FORBIDDEN, 40108, "인증 불가, 관리자에게 문의하세요."),
	EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 40109, "재 로그인이 필요합니다."),
	EXISTS_PHONE_NUMBER(HttpStatus.CONFLICT, 40110, "이미 가입된 전화번호입니다."),
	OVER_MAX_MESSAGE_COUNT(HttpStatus.FORBIDDEN, 40111, "인증코드 요청 횟수를 초과하였습니다."),
	OVER_MAX_VERIFIED_COUNT(HttpStatus.FORBIDDEN, 40112, "인증코드 확인 횟수를 초과하였습니다."),
	EXISTS_USER_ID(HttpStatus.CONFLICT, 40113, "이미 가입된 아이디입니다."),
	NOT_SEND_PHONE_NUMBER(HttpStatus.NOT_FOUND, 40114, "인증 요청이 보내지 않은 전화번호입니다."),
	INVALID_VERIFIED_CODE(HttpStatus.FORBIDDEN, 40115, "인증 코드가 일치하지 않습니다."),

	/* 499xx ETC */
	NOT_FOUND_RESOURCE(HttpStatus.NOT_FOUND, 49901, "해당 경로를 찾을 수 없습니다."),
	METHOD_FORBIDDEN(HttpStatus.METHOD_NOT_ALLOWED, 49902, "지원하지 않는 HTTP 메서드를 사용합니다."),
	;

	private final HttpStatus httpStatus;
	private final int errorCode;
	private final String message;
}
