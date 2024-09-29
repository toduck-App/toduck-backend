package im.toduck.domain.user.common.mapper;

import im.toduck.domain.user.persistence.entity.Block;
import im.toduck.domain.user.persistence.entity.User;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BlockMapper {
	public static Block toBlock(User blocker, User blockedUser) {
		return Block.builder()
			.blocker(blocker)
			.blocked(blockedUser)
			.build();
	}
}
