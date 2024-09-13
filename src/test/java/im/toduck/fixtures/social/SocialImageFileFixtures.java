package im.toduck.fixtures.social;

import java.util.ArrayList;
import java.util.List;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialImageFile;

public class SocialImageFileFixtures {

	/**
	 * 여러 개의 SocialImageFile을 생성합니다.
	 *
	 * @param social   해당 게시글
	 * @param imageUrls 이미지 URL 목록
	 * @return 생성된 SocialImageFile 리스트
	 */
	public static List<SocialImageFile> MULTIPLE_IMAGE_FILES(Social social, List<String> imageUrls) {
		List<SocialImageFile> imageFiles = new ArrayList<>();
		for (String url : imageUrls) {
			imageFiles.add(SocialImageFile.builder()
				.social(social)
				.url(url)
				.build());
		}
		return imageFiles;
	}

}
