package im.toduck.infra.s3.presentation.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import im.toduck.infra.s3.domain.usecase.S3UseCase;
import im.toduck.infra.s3.presentation.api.S3ControllerApi;
import im.toduck.infra.s3.presentation.dto.request.PreSignedUrlRequest;
import im.toduck.infra.s3.presentation.dto.response.PreSignedUrlResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/presigned")
@RequiredArgsConstructor
public class S3Controller implements S3ControllerApi {
	private final S3UseCase s3UseCase;

	@Override
	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<PreSignedUrlResponse>> getPresignedUrl(
		CustomUserDetails userDetails,
		PreSignedUrlRequest request
	) {
		return ResponseEntity.ok().body(
			ApiResponse.createSuccess(
				s3UseCase.generatePresignedUrl(request, userDetails.getUserId(), LocalDate.now())));
	}
}
