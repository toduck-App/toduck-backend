package im.toduck.domain.social.domain.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SocialCreatedEvent {
	private final Long userId;
}
