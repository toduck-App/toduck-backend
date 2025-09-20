package im.toduck.domain.backoffice.common.mapper;

import java.util.List;

import org.springframework.data.domain.Page;

import im.toduck.domain.backoffice.presentation.dto.response.UserListPaginationResponse;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserManagementMapper {

	public static UserListPaginationResponse toUserListPaginationResponse(final Page<User> userPage) {
		List<UserListPaginationResponse.UserInfo> users = userPage.getContent()
			.stream()
			.map(UserManagementMapper::toUserInfo)
			.toList();

		UserListPaginationResponse.PageInfo pageInfo = UserListPaginationResponse.PageInfo.builder()
			.currentPage(userPage.getNumber())
			.pageSize(userPage.getSize())
			.totalPages(userPage.getTotalPages())
			.totalElements(userPage.getTotalElements())
			.first(userPage.isFirst())
			.last(userPage.isLast())
			.hasNext(userPage.hasNext())
			.hasPrevious(userPage.hasPrevious())
			.build();

		return UserListPaginationResponse.builder()
			.users(users)
			.pageInfo(pageInfo)
			.build();
	}

	private static UserListPaginationResponse.UserInfo toUserInfo(final User user) {
		boolean isSuspended = user.isSuspended();

		return UserListPaginationResponse.UserInfo.builder()
			.id(user.getId())
			.nickname(user.getNickname())
			.phoneNumber(user.getPhoneNumber())
			.loginId(user.getLoginId())
			.email(user.getEmail())
			.imageUrl(user.getImageUrl())
			.role(user.getRole())
			.provider(user.getProvider())
			.suspended(isSuspended)
			.suspendedUntil(isSuspended ? user.getSuspendedUntil() : null)
			.suspensionReason(isSuspended ? user.getSuspensionReason() : null)
			.createdAt(user.getCreatedAt())
			.updatedAt(user.getUpdatedAt())
			.build();
	}
}
