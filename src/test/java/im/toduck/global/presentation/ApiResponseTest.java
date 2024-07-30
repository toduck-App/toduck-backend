package im.toduck.global.presentation;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.exception.VoException;

class ApiResponseTest {

	private void verifyResponse(int code, Object content, String message, ApiResponse<?> response) {
		assertThat(response.getCode()).isEqualTo(code);
		assertThat(response.getContent()).isEqualTo(content);
		assertThat(response.getMessage()).isEqualTo(message);
	}

	@Test
	void 성공응답이_생성되었을_때_code는_20000이고_content는_성공이며_message는_null이어야한다() {
		// given
		String content = "성공";

		// when
		ApiResponse<String> response = ApiResponse.createSuccess(content);

		// then
		verifyResponse(ApiResponse.SUCCESS_CODE, content, null, response);
	}

	@Test
	void 유효성검사오류응답이_생성되었을_때_code는_30001이고_content는_오류맵이며_message는_유효성검사오류메시지여야한다() {
		// given
		Map<String, String> errors = new HashMap<>();
		errors.put("field", "error");

		// when
		ApiResponse<Map<String, String>> response = ApiResponse.createValidationError(errors);

		// then
		verifyResponse(ApiResponse.VALIDATION_ERROR_CODE, errors, ApiResponse.VALIDATION_ERROR_MESSAGE, response);
	}

	@Test
	void VO오류응답이_생성되었을_때_code는_30002이고_content는_예외메시지이며_message는_VO오류메시지여야한다() {
		// given
		VoException ex = new VoException("VO 오류");

		// when
		ApiResponse<String> response = ApiResponse.createVoError(ex);

		// then
		verifyResponse(ApiResponse.VO_ERROR_CODE, ex.getMessage(), ApiResponse.VO_ERROR_MESSAGE, response);
	}

	@Test
	void 일반오류응답이_생성되었을_때_code는_예외코드의_code이고_content는_null이며_message는_예외코드의_메시지여야한다() {
		// given
		ExceptionCode ec = ExceptionCode.NOT_FOUND_RESOURCE;

		// when
		ApiResponse<Object> response = ApiResponse.createError(ec);

		// then
		verifyResponse(ec.getErrorCode(), null, ec.getMessage(), response);
	}

	@Test
	void 공통예외응답이_생성되었을_때_code는_예외코드의_code이고_content는_null이며_message는_예외코드의_메시지여야한다() {
		// given
		CommonException ex = CommonException.from(ExceptionCode.NOT_FOUND_RESOURCE);

		// when
		ApiResponse<Object> response = ApiResponse.createErrorFromException(ex);

		// then
		verifyResponse(ex.getErrorCode(), null, ex.getMessage(), response);
	}

	@Test
	void 서버오류응답이_생성되었을_때_code는_50001이고_content는_null이며_message는_서버오류메시지여야한다() {
		// given

		// when
		ApiResponse<Object> response = ApiResponse.createServerError();

		// then
		verifyResponse(ApiResponse.SERVER_ERROR_CODE, null, ApiResponse.SERVER_ERROR_MESSAGE, response);
	}
}
