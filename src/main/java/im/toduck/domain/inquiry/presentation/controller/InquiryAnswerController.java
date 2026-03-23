package im.toduck.domain.inquiry.presentation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.inquiry.domain.usecase.InquiryAnswerUseCase;
import im.toduck.domain.inquiry.presentation.api.InquiryAnswerApi;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryAnswerCreateRequest;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryAnswerUpdateRequest;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/inquiry-answers")
public class InquiryAnswerController implements InquiryAnswerApi {

	private final InquiryAnswerUseCase inquiryAnswerUseCase;

	@Override
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<Map<String, Object>>> createInquiryAnswer(
		@RequestBody @Valid final InquiryAnswerCreateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		inquiryAnswerUseCase.createInquiryAnswer(request, userDetails.getUserId());

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PatchMapping("/{inquiryId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<Map<String, Object>>> updateInquiryAnswer(
		@PathVariable final Long inquiryId,
		@RequestBody @Valid final InquiryAnswerUpdateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		inquiryAnswerUseCase.updateInquiryAnswer(inquiryId, request, userDetails.getUserId());

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@DeleteMapping("/{inquiryId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteInquiryAnswer(
		@PathVariable final Long inquiryId
	) {
		inquiryAnswerUseCase.deleteInquiryAnswer(inquiryId);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}
}
