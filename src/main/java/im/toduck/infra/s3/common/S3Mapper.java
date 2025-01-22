package im.toduck.infra.s3.common;

import java.net.URL;

import im.toduck.infra.s3.presentation.dto.response.FileUrlDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class S3Mapper {

	public static FileUrlDto toFileUrlDto(final URL presignedUrl, final String fileUrl) {
		return FileUrlDto.builder()
			.presignedUrl(presignedUrl)
			.fileUrl(fileUrl)
			.build();
	}
}
