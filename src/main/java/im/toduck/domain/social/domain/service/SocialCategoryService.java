package im.toduck.domain.social.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import im.toduck.domain.social.common.mapper.SocialCategoryMapper;
import im.toduck.domain.social.persistence.entity.Social;
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

	public List<SocialCategoryDto> getSocialCategoryDtosBySocial(final Social social) {
		List<SocialCategoryLink> socialCategoryLinks = socialCategoryLinkRepository.findAllBySocial(social); // 1. 링크 조회
		return socialCategoryLinks.stream()
			.map(SocialCategoryLink::getSocialCategory)
			.map(SocialCategoryMapper::toSocialCategoryDto)
			.toList();
	}
}
