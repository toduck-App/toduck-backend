package im.toduck.fixtures.user;

import im.toduck.domain.user.persistence.entity.Block;
import im.toduck.domain.user.persistence.entity.User;

public class BlockFixtures {

	public static Block BLOCK_USER(User blocker, User blocked) {
		return Block.builder()
			.blocker(blocker)
			.blocked(blocked)
			.build();
	}
}
