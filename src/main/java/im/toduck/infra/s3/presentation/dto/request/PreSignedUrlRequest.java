package im.toduck.infra.s3.presentation.dto.request;

import java.util.List;

public record PreSignedUrlRequest(
	List<FileNameDto> fileNameDtos
) {
}
