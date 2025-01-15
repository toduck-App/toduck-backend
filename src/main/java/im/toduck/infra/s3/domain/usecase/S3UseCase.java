package im.toduck.infra.s3.domain.usecase;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import im.toduck.global.annotation.UseCase;
import im.toduck.infra.s3.common.S3Mapper;
import im.toduck.infra.s3.domain.service.S3Service;
import im.toduck.infra.s3.presentation.dto.request.PreSignedUrlRequest;
import im.toduck.infra.s3.presentation.dto.response.FileUrlDto;
import im.toduck.infra.s3.presentation.dto.response.PreSignedUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class S3UseCase {

	private final S3Service s3Service;

	public PreSignedUrlResponse generatePresignedUrl(
		final PreSignedUrlRequest request,
		final Long userId,
		final LocalDate currentDate
	) {
		List<FileUrlDto> fileUrlDtos = request.fileNameDtos().stream()
			.map(fileNameDto -> generateFileUrl(fileNameDto.fileName(), userId, currentDate))
			.toList();

		log.info("presigned URL 생성 - UserId: {}, File 개수: {}", userId, request.fileNameDtos().size());
		return new PreSignedUrlResponse(fileUrlDtos);
	}

	private FileUrlDto generateFileUrl(final String fileName, final Long userId, final LocalDate currentDate) {
		String objectKey = s3Service.createObjectKey(fileName, userId, currentDate);
		URL presignedUrl = s3Service.generatePresignedUrl(objectKey);
		String fileUrl = s3Service.generateFileUrl(objectKey);

		return S3Mapper.toFileUrlDto(presignedUrl, fileUrl);
	}
}
