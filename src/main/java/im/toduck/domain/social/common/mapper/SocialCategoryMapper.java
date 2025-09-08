package im.toduck.domain.social.common.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.presentation.dto.response.SocialCategoryResponse;
import im.toduck.domain.social.presentation.dto.response.SocialCategoryResponse.SocialCategoryDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialCategoryMapper {

	public static SocialCategoryResponse toSocialCategoryResponse(final List<SocialCategoryDto> socialCategories) {
		return SocialCategoryResponse.builder()
			.categories(socialCategories)
			.build();
	}

	public static SocialCategoryDto toSocialCategoryDto(final SocialCategory socialCategory) {
		return SocialCategoryDto.builder()
			.socialCategoryId(socialCategory.getId())
			.name(socialCategory.getName())
			.build();
	}

	public static Map<Long, List<SocialCategoryDto>> toCategoryDtoMap(
		Map<Long, List<SocialCategory>> categoriesEntityMap
	) {
		return categoriesEntityMap.entrySet().stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				entry -> entry.getValue().stream()
					.map(SocialCategoryMapper::toSocialCategoryDto)
					.toList()
			));
	}
}
