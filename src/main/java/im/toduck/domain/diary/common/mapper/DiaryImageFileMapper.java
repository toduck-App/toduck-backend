package im.toduck.domain.diary.common.mapper;

import im.toduck.domain.diary.persistence.entity.Diary;
import im.toduck.domain.diary.persistence.entity.DiaryImage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiaryImageFileMapper {
	public static DiaryImage toDiaryImageFile(Diary diary, String url) {
		return DiaryImage.builder()
			.diary(diary)
			.url(url)
			.build();
	}
}
