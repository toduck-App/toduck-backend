package im.toduck.domain.user.domain.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserSignedUpEvent {
	private final Long userId;
}
