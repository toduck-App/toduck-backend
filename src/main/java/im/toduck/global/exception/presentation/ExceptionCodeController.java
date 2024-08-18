package im.toduck.global.exception.presentation;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import im.toduck.global.exception.ExceptionCode;

@Controller
public class ExceptionCodeController {

	@PreAuthorize("isAnonymous()")
	@GetMapping("/exception-codes")
	public String showExceptionCodes(Model model) {
		List<ExceptionCodeInfo> exceptionCodes = Arrays.stream(ExceptionCode.values())
			.map(ExceptionCodeInfo::from)
			.toList();

		model.addAttribute("exceptionCodes", exceptionCodes);
		return "exception-codes-view";
	}

	private record ExceptionCodeInfo(
		String name,
		int httpStatusCode,
		String httpStatusReasonPhrase,
		int errorCode,
		String message,
		String description
	) {
		public static ExceptionCodeInfo from(ExceptionCode code) {
			return new ExceptionCodeInfo(
				code.name(),
				code.getHttpStatus().value(),
				code.getHttpStatus().getReasonPhrase(),
				code.getErrorCode(),
				code.getMessage(),
				code.getDescription()
			);
		}
	}
}
