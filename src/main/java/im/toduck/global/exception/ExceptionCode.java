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
	INVALID_PHONE_NUMBER_OR_PASSWORD(HttpStatus.UNAUTHORIZED, 40101, "전화번호 또는 비밀번호가 일치하지 않습니다.",
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

	/* 402xx */
	NOT_FOUND_USER(HttpStatus.NOT_FOUND, 40201, "사용자를 찾을 수 없습니다."),

	/* 404xx */
	NOT_FOUND_SOCIAL_BOARD(HttpStatus.NOT_FOUND, 40401, "게시글을 찾을 수 없습니다."),
	UNAUTHORIZED_ACCESS_SOCIAL_BOARD(HttpStatus.FORBIDDEN, 40402, "게시글에 접근 권한이 없습니다."),

	/* 499xx ETC */
	NOT_FOUND_RESOURCE(HttpStatus.NOT_FOUND, 49901, "해당 경로를 찾을 수 없습니다."),
	METHOD_FORBIDDEN(HttpStatus.METHOD_NOT_ALLOWED, 49902, "지원하지 않는 HTTP 메서드를 사용합니다.");

	private final HttpStatus httpStatus;
	private final int errorCode;
	private final String message;
	private final String description;

	ExceptionCode(HttpStatus httpStatus, int errorCode, String message) {
		this(httpStatus, errorCode, message, "");
	}
}
