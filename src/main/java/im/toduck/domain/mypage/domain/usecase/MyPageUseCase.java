package im.toduck.domain.mypage.domain.usecase;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.mypage.common.mapper.MyPageMapper;
import im.toduck.domain.mypage.domain.service.MyPageService;
import im.toduck.domain.mypage.presentation.dto.request.NickNameUpdateRequest;
import im.toduck.domain.mypage.presentation.dto.request.ProfileImageUpdateRequest;
import im.toduck.domain.mypage.presentation.dto.response.BlockedUsersResponse;
import im.toduck.domain.mypage.presentation.dto.response.MyCommentsResponse;
import im.toduck.domain.mypage.presentation.dto.response.NickNameResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.dto.response.CursorPaginationResponse;
import im.toduck.global.util.PaginationUtil;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class MyPageUseCase {
	private static final int DEFAULT_COMMENT_PAGE_SIZE = 20;

	private final UserService userService;
	private final MyPageService myPageService;

	@Transactional
	public void updateNickname(Long userId, NickNameUpdateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		myPageService.updateUniqueNickname(user, request.nickname());
	}

	public NickNameResponse getMyNickname(Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		return NickNameResponse.builder()
			.nickname(user.getNickname())
			.build();
	}

	@Transactional
	public void updateProfileImage(Long userId, ProfileImageUpdateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		myPageService.updateProfileImage(user, request.imageUrl());
	}

	@Transactional(readOnly = true)
	public BlockedUsersResponse getBlockedUsers(final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		List<User> blockedUsers = myPageService.getBlockedUsers(user);

		return MyPageMapper.toBlockedUsersResponse(blockedUsers);
	}

	@Transactional(readOnly = true)
	public CursorPaginationResponse<MyCommentsResponse> getMyComments(
		final Long userId,
		final Long cursor,
		final Integer limit
	) {

		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		int actualLimit = PaginationUtil.resolveLimit(limit, DEFAULT_COMMENT_PAGE_SIZE);
		int fetchLimit = PaginationUtil.calculateTotalFetchSize(actualLimit);

		List<MyCommentsResponse> comments = myPageService.getMyCommentsResponse(user.getId(), cursor, fetchLimit);

		boolean hasMore = PaginationUtil.hasMore(comments, actualLimit);

		Long nextCursor = PaginationUtil.getNextCursor(
			hasMore,
			comments,
			actualLimit,
			res -> res.comment().commentId()
		);

		List<MyCommentsResponse> pagedComments = comments.stream()
			.limit(actualLimit)
			.toList();

		return PaginationUtil.toCursorPaginationResponse(hasMore, nextCursor, pagedComments);
	}
}
