package im.toduck.domain.inquiry.presentation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.inquiry.domain.usecase.InquiryUseCase;
import im.toduck.domain.inquiry.presentation.api.InquiryApi;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryCreateRequest;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryUpdateRequest;
import im.toduck.domain.inquiry.presentation.dto.response.InquiryListResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/inquiries")
public class InquiryController implements InquiryApi {
	private final InquiryUseCase inquiryUseCase;

	@Override
	@GetMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<InquiryListResponse>> getInquiries(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		InquiryListResponse response = inquiryUseCase.getInquiries(userDetails.getUserId());

		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> createInquiry(
		@RequestBody @Valid final InquiryCreateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		inquiryUseCase.createInquiry(request, userDetails.getUserId());

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PatchMapping("/{inquiryId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> updateInquiry(
		@PathVariable final Long inquiryId,
		@RequestBody @Valid final InquiryUpdateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		inquiryUseCase.updateInquiry(inquiryId, request, userDetails.getUserId());

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@DeleteMapping("/{inquiryId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteInquiry(
		@PathVariable final Long inquiryId,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		inquiryUseCase.deleteInquiry(inquiryId, userDetails.getUserId());

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<InquiryListResponse>> getAllInquiries(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		InquiryListResponse response = inquiryUseCase.getAllInquiries(userDetails.getUserId());

		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}
}
