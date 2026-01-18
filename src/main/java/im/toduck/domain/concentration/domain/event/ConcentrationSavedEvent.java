package im.toduck.domain.concentration.domain.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ConcentrationSavedEvent {
	private final Long userId;
}
