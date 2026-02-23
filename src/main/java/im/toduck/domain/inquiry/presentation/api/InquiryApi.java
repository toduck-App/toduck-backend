package im.toduck.domain.inquiry.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.domain.inquiry.presentation.dto.request.InquiryCreateRequest;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryUpdateRequest;
import im.toduck.domain.inquiry.presentation.dto.response.InquiryListResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Inquiry")
public interface InquiryApi {
	@Operation(
		summary = "문의 목록 조회",
		description = "사용자가 자신의 문의 목록을 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = InquiryListResponse.class,
			description = "문의 목록 조회 성공. 문의 목록을 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<InquiryListResponse>> getInquiries(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "문의 생성",
		description = "사용자가 문의를 생성합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "문의 생성 성공. 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_ADMIN)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> createInquiry(
		@RequestBody @Valid final InquiryCreateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "문의 수정",
		description =
			"""
				문의를 수정합니다. <br/>
				"inquiryId" : 문의 일련번호 <br/>
				"type" : 문의 타입 [ERROR, USAGE, SUGGESTION, ETC]<br/>
				"inquiryImgs" : 문의 이미지 url <br/><br/>
				수정하지 않을 경우 null을 입력하면 됩니다. 이미지의 경우 빈 리스트를 넣으면 이미지가 전부 삭제됩니다.
				"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "문의 수정 성공. 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_INQUIRY)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> updateInquiry(
		@PathVariable final Long inquiryId,
		@RequestBody @Valid final InquiryUpdateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "문의 삭제",
		description = "문의를 삭제합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "문의 삭제 성공, 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_INQUIRY)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> deleteInquiry(
		@PathVariable final Long inquiryId,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "전체 문의 목록 조회",
		description = "관리자가 전체 문의 목록을 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = InquiryListResponse.class,
			description = "전체 문의 목록 조회 성공. 문의 목록을 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<InquiryListResponse>> getAllInquiries(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);
}
