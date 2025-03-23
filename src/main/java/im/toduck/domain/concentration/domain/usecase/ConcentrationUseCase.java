package im.toduck.domain.concentration.domain.usecase;

import im.toduck.domain.concentration.domain.service.ConcentrationService;
import im.toduck.domain.concentration.persistence.entity.Concentration;
import im.toduck.domain.concentration.presentation.dto.request.ConcentrationRequest;
import im.toduck.domain.concentration.presentation.dto.response.ConcentrationSaveResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class ConcentrationUseCase {
	private final UserService userService;
	private final ConcentrationService concentrationService;

	@Transactional
	public ConcentrationSaveResponse saveConcentration(Long userId, ConcentrationRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		Concentration concentration = concentrationService.saveConcentration(user, request);

		log.info("집중 저장 - UserId: {}, ConcentrationId: {}", userId, concentration.getId());
		return new ConcentrationSaveResponse(concentration.getId());
	}
}
