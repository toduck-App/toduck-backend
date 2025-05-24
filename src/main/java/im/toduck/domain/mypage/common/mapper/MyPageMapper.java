package im.toduck.domain.mypage.common.mapper;

import java.util.List;

import im.toduck.domain.mypage.presentation.dto.response.BlockedUsersResponse;
import im.toduck.domain.mypage.presentation.dto.response.BlockedUsersResponse.BlockedUser;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyPageMapper {

	public static BlockedUsersResponse toBlockedUsersResponse(final List<User> blockedUsers) {
		List<BlockedUser> blockedUserDtos = blockedUsers.stream()
			.map(MyPageMapper::toBlockedUser)
			.toList();

		return BlockedUsersResponse.builder()
			.blockedUsers(blockedUserDtos)
			.build();
	}

	public static BlockedUser toBlockedUser(final User user) {
		return BlockedUser.builder()
			.userId(user.getId())
			.nickname(user.getNickname())
			.profileImageUrl(user.getImageUrl())
			.build();

	}
}
