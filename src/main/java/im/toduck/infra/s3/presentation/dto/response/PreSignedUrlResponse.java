package im.toduck.infra.s3.presentation.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record PreSignedUrlResponse(
	List<FileUrlDto> fileUrlsDtos
) {
	public static PreSignedUrlResponse from(List<FileUrlDto> fileUrlDtos) {
		return new PreSignedUrlResponse(fileUrlDtos);
	}
}
