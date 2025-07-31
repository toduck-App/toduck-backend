package im.toduck.domain.diary.common.mapper;

import im.toduck.domain.diary.persistence.entity.MasterKeyword;
import im.toduck.domain.diary.persistence.entity.UserKeyword;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserKeywordMapper {

	public static UserKeyword fromMasterKeyword(User user, MasterKeyword masterKeyword) {
		return UserKeyword.builder()
			.user(user)
			.category(masterKeyword.getCategory())
			.keyword(masterKeyword.getKeyword())
			.count(0L)
			.build();
	}
}
