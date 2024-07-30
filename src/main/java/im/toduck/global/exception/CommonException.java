package im.toduck.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonException extends RuntimeException {
	private final HttpStatus httpStatus;
	private final int errorCode;
	private final String message;

	public static CommonException from(ExceptionCode ex) {
		return new CommonException(ex.getHttpStatus(), ex.getErrorCode(), ex.getMessage());
	}

	public boolean isExceptionCode(ExceptionCode ex) {
		return this.httpStatus == ex.getHttpStatus()
			&& this.errorCode == ex.getErrorCode()
			&& this.message.equals(ex.getMessage());
	}
}
