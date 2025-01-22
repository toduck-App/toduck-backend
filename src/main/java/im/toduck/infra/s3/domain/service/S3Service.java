package im.toduck.infra.s3.domain.service;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import im.toduck.infra.s3.presentation.dto.ImageExtension;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Service {
	private static final long PRESIGNED_URL_EXPIRATION_MINUTES = 2;
	private static final String PATH_DELIMITER = "/";
	private static final String USER_DIRECTORY_PREFIX = "users";

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.s3.endpoint}")
	private String endPoint;

	private final AmazonS3 amazonS3;

	public String createObjectKey(final String fileName, final Long userId, final LocalDate createdAt) {
		String directoryPath = createDirectoryPath(userId, createdAt);
		String uniqueFileName = createUniqueFileName(fileName);
		return String.join(PATH_DELIMITER, directoryPath, uniqueFileName);
	}

	private String createDirectoryPath(final Long userId, final LocalDate createdAt) {
		return String.format("%s/%s/%d/%d/%d",
			USER_DIRECTORY_PREFIX,
			userId,
			createdAt.getYear(),
			createdAt.getMonthValue(),
			createdAt.getDayOfMonth()
		);
	}

	private String createUniqueFileName(final String originalFileName) {
		return String.format("%s_%s", UUID.randomUUID(), originalFileName);
	}

	public String generateFileUrl(final String objectKey) {
		return String.join(PATH_DELIMITER, endPoint, objectKey);
	}

	public URL generatePresignedUrl(final String objectKey, final String extension) {
		String mimeType = ImageExtension.findMimeType(extension.toLowerCase());

		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, objectKey)
			.withMethod(HttpMethod.PUT)
			.withExpiration(calculateExpirationDate())
			.withContentType(mimeType);
		request.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());

		return amazonS3.generatePresignedUrl(request);
	}

	private Date calculateExpirationDate() {
		return Date.from(
			Instant.now().plus(PRESIGNED_URL_EXPIRATION_MINUTES, ChronoUnit.MINUTES)
		);
	}
}
