package im.toduck.domain.diary.domain.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DiaryCreatedEvent {
	private final Long userId;
}
