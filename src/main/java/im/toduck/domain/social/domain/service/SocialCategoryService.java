package im.toduck.domain.social.domain.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import im.toduck.domain.social.common.mapper.SocialCategoryMapper;
import im.toduck.domain.social.persistence.entity.SocialCategoryLink;
import im.toduck.domain.social.persistence.repository.SocialCategoryLinkRepository;
import im.toduck.domain.social.presentation.dto.response.SocialCategoryResponse.SocialCategoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialCategoryService {
	private final SocialCategoryLinkRepository socialCategoryLinkRepository;

	public Map<Long, List<SocialCategoryDto>> getSocialCategoryDtosBySocialIds(final List<Long> socialIds) {
		if (socialIds == null || socialIds.isEmpty()) {
			return Map.of();
		}

		List<SocialCategoryLink> socialCategoryLinks = socialCategoryLinkRepository.findAllBySocialIdInWithCategory(
			socialIds);

		return socialCategoryLinks.stream()
			.collect(Collectors.groupingBy(
				link -> link.getSocial().getId(),
				Collectors.mapping(
					link -> SocialCategoryMapper.toSocialCategoryDto(link.getSocialCategory()),
					Collectors.toList()
				)
			));
	}
}
