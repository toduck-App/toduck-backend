package im.toduck.domain.social.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.social.common.mapper.SocialCategoryLinkMapper;
import im.toduck.domain.social.common.mapper.SocialImageFileMapper;
import im.toduck.domain.social.common.mapper.SocialMapper;
import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.Like;
import im.toduck.domain.social.persistence.entity.Report;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.persistence.entity.SocialCategoryLink;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.social.persistence.repository.CommentRepository;
import im.toduck.domain.social.persistence.repository.LikeRepository;
import im.toduck.domain.social.persistence.repository.ReportRepository;
import im.toduck.domain.social.persistence.repository.SocialCategoryLinkRepository;
import im.toduck.domain.social.persistence.repository.SocialCategoryRepository;
import im.toduck.domain.social.persistence.repository.SocialImageFileRepository;
import im.toduck.domain.social.persistence.repository.SocialRepository;
import im.toduck.domain.social.presentation.dto.request.SocialCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialUpdateRequest;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialBoardService {
	private final SocialRepository socialRepository;
	private final SocialCategoryRepository socialCategoryRepository;
	private final SocialImageFileRepository socialImageFileRepository;
	private final SocialCategoryLinkRepository socialCategoryLinkRepository;
	private final CommentRepository commentRepository;
	private final LikeRepository likeRepository;
	private final ReportRepository reportRepository;

	@Transactional(readOnly = true)
	public Optional<Social> getSocialById(Long socialId) {
		return socialRepository.findById(socialId);
	}

	@Transactional
	public Social createSocialBoard(User user, SocialCreateRequest request) {
		Social socialBoard = SocialMapper.toSocial(user, request.content(), request.isAnonymous());
		return socialRepository.save(socialBoard);
	}

	@Transactional
	public void deleteSocialBoard(User user, Social socialBoard) {
		if (!isBoardOwner(socialBoard, user)) {
			log.warn("권한이 없는 유저가 게시글 삭제 시도 - UserId: {}, SocialBoardId: {}", user.getId(), socialBoard.getId());
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD);
		}

		List<SocialImageFile> imageFiles = socialImageFileRepository.findAllBySocial(socialBoard);
		imageFiles.forEach(SocialImageFile::softDelete);

		List<SocialCategoryLink> socialCategoryLinks = socialCategoryLinkRepository.findAllBySocial(socialBoard);
		socialCategoryLinks.forEach(SocialCategoryLink::softDelete);

		List<Comment> comments = commentRepository.findAllBySocial(socialBoard);
		comments.forEach(Comment::softDelete);

		List<Like> likes = likeRepository.findAllBySocial(socialBoard);
		likes.forEach(Like::softDelete);

		socialRepository.delete(socialBoard);
	}

	@Transactional
	public void updateSocialBoard(User user, Social socialBoard, SocialUpdateRequest request) {
		if (!isBoardOwner(socialBoard, user)) {
			log.warn("권한이 없는 유저가 소셜 게시판 수정 시도 - UserId: {}, SocialBoardId: {}", user.getId(), socialBoard.getId());
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD);
		}

		if (request.socialCategoryIds() != null) {
			if (request.socialCategoryIds().isEmpty()) {
				log.warn("게시글 업데이트시 빈 카테고리 리스트로 소셜 게시판 수정 시도 - UserId: {}, SocialBoardId: {}", user.getId(),
					socialBoard.getId());
				throw CommonException.from(ExceptionCode.EMPTY_SOCIAL_CATEGORY_LIST);
			}

			List<SocialCategory> socialCategories = findAllSocialCategories(request.socialCategoryIds());

			if (isInvalidCategoryIncluded(request.socialCategoryIds(), socialCategories)) {
				log.warn("게시글 업데이트시 유효하지 않은 카테고리가 포함됨 - UserId: {}, SocialBoardId: {}, CategoryIds: {}", user.getId(),
					socialBoard.getId(), request.socialCategoryIds());
				throw CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY);
			}

			socialCategoryLinkRepository.deleteAllBySocial(socialBoard);
			addSocialCategoryLinks(request.socialCategoryIds(), socialCategories, socialBoard);
		}

		if (request.content() != null) {
			socialBoard.updateContent(request.content());
		}

		if (request.isAnonymous() != null) {
			socialBoard.updateIsAnonymous(request.isAnonymous());
		}

		if (request.socialImageUrls() != null) {
			socialImageFileRepository.deleteAllBySocial(socialBoard);
			addSocialImageFiles(request.socialImageUrls(), socialBoard);
		}
	}

	@Transactional(readOnly = true)
	public List<SocialCategory> findAllSocialCategories(List<Long> socialCategoryIds) {
		return socialCategoryRepository.findAllById(socialCategoryIds);
	}

	@Transactional
	public void addSocialImageFiles(List<String> imageUrls, Social socialBoard) {
		List<SocialImageFile> socialImageFiles = imageUrls.stream()
			.map(url -> SocialImageFileMapper.toSocialImageFile(socialBoard, url))
			.toList();
		socialImageFileRepository.saveAll(socialImageFiles);
	}

	@Transactional
	public void addSocialCategoryLinks(List<Long> categoryIds, List<SocialCategory> socialCategories,
		Social socialBoard) {
		if (isInvalidCategoryIncluded(categoryIds, socialCategories)) {
			throw CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY);
		}

		List<SocialCategoryLink> socialCategoryLinks = socialCategories.stream()
			.map(sc -> SocialCategoryLinkMapper.toSocialCategoryLink(socialBoard, sc))
			.toList();

		socialCategoryLinkRepository.saveAll(socialCategoryLinks);
	}

	@Transactional(readOnly = true)
	public List<SocialImageFile> getSocialImagesBySocial(Social socialBoard) {
		return socialImageFileRepository.findAllBySocial(socialBoard);
	}

	@Transactional(readOnly = true)
	public List<Social> getSocials(Long cursor, Integer limit, Long currentUserId) {
		PageRequest pageRequest = PageRequest.of(PaginationUtil.FIRST_PAGE_INDEX, limit);
		return socialRepository.findByIdBeforeOrderByIdDescExcludingBlocked(cursor, currentUserId, pageRequest);
	}

	@Transactional(readOnly = true)
	public List<Social> findLatestSocials(int limit, Long currentUserId) {
		PageRequest pageRequest = PageRequest.of(PaginationUtil.FIRST_PAGE_INDEX, limit);
		return socialRepository.findLatestSocialsExcludingBlocked(currentUserId, pageRequest);
	}

	@Transactional
	public Report createReport(User user, Social social, String reason) {
		Report report = ReportMapper.toReport(user, social, reason);
		return reportRepository.save(report);
	}

	@Transactional(readOnly = true)
	public boolean existsByUserAndSocial(User user, Social social) {
		return reportRepository.existsByUserAndSocial(user, social);
	}

	private boolean isBoardOwner(Social socialBoard, User user) {
		return socialBoard.isOwner(user);
	}

	private boolean isInvalidCategoryIncluded(List<Long> socialCategoryIds, List<SocialCategory> socialCategories) {
		return socialCategories.size() != socialCategoryIds.size();
	}
}

