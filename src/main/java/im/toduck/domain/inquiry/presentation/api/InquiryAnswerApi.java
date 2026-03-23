package im.toduck.domain.inquiry.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.domain.inquiry.presentation.dto.request.InquiryAnswerCreateRequest;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryAnswerUpdateRequest;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "InquiryAnswer")
public interface InquiryAnswerApi {
	@Operation(
		summary = "문의 답변 생성",
		description = "관리자가 문의 답변을 생성합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "문의 답변 생성 성공. 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_ADMIN),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_INQUIRY),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.ALREADY_ANSWERED_INQUIRY)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> createInquiryAnswer(
		@RequestBody @Valid final InquiryAnswerCreateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "문의 답변 수정",
		description = "관리자가 문의 답변을 수정합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "문의 답변 수정 성공. 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_ADMIN),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_INQUIRY)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> updateInquiryAnswer(
		@PathVariable final Long inquiryId,
		@RequestBody @Valid final InquiryAnswerUpdateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "문의 답변 삭제",
		description = "관리자가 문의 답변을 삭제합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "문의 답변 삭제 성공. 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_INQUIRY),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_INQUIRY_ANSWER)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> deleteInquiryAnswer(
		@PathVariable final Long inquiryId
	);
}
