package im.toduck.global.presentation;

import java.util.Map;

import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.exception.VoException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ApiResponse<T> {

	public static final int SUCCESS_CODE = 20000;
	public static final int VALIDATION_ERROR_CODE = 30001;
	public static final int VO_ERROR_CODE = 30002;
	public static final int SERVER_ERROR_CODE = 50001;
	public static final String VO_ERROR_MESSAGE = "데이터 형식이 유효하지 않습니다.";
	public static final String VALIDATION_ERROR_MESSAGE = "잘못된 요청 형식입니다.";
	public static final String SERVER_ERROR_MESSAGE = "서버 오류입니다.";

	private final int code;
	private final T content;
	private final String message;

	/**
	 * 성공 응답을 생성합니다.
	 *
	 * @param content 응답 내용.
	 * @param <T> 응답 내용의 타입.
	 * @return 성공 응답.
	 */
	public static <T> ApiResponse<T> createSuccess(T content) {
		return new ApiResponse<>(SUCCESS_CODE, content, null);
	}

	/**
	 * 유효성 검사 오류 응답을 생성합니다.
	 *
	 * @param errors 오류 맵.
	 * @return 유효성 검사 오류 응답.
	 */
	public static ApiResponse<Map<String, String>> createValidationError(Map<String, String> errors) {
		return new ApiResponse<>(VALIDATION_ERROR_CODE, errors, VALIDATION_ERROR_MESSAGE);
	}

	/**
	 * VO 오류 응답을 생성합니다.
	 *
	 * @param ex VO 예외.
	 * @return VO 오류 응답.
	 */
	public static ApiResponse<String> createVoError(VoException ex) {
		return new ApiResponse<>(VO_ERROR_CODE, ex.getMessage(), VO_ERROR_MESSAGE);
	}

	/**
	 * 일반 오류 응답을 생성합니다.
	 *
	 * @param ec 예외 코드.
	 * @param <T> 응답 내용의 타입.
	 * @return 오류 응답.
	 */
	public static <T> ApiResponse<T> createError(ExceptionCode ec) {
		return new ApiResponse<>(ec.getErrorCode(), null, ec.getMessage());
	}

	/**
	 * 일반 오류 응답을 생성합니다.
	 *
	 * @param ex 공통 예외.
	 * @param <T> 응답 내용의 타입.
	 * @return 오류 응답.
	 */
	public static <T> ApiResponse<T> createErrorFromException(CommonException ex) {
		return new ApiResponse<>(ex.getErrorCode(), null, ex.getMessage());
	}

	/**
	 * 서버 오류 응답을 생성합니다.
	 *
	 * @param <T> 응답 내용의 타입.
	 * @return 서버 오류 응답.
	 */
	public static <T> ApiResponse<T> createServerError() {
		return new ApiResponse<>(SERVER_ERROR_CODE, null, SERVER_ERROR_MESSAGE);
	}
}
