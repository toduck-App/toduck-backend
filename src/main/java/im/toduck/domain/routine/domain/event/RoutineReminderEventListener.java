package im.toduck.domain.routine.domain.event;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import im.toduck.domain.routine.domain.service.RoutineReminderSchedulerService;
import im.toduck.domain.routine.domain.service.RoutineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoutineReminderEventListener {

	private final RoutineService routineService;
	private final RoutineReminderSchedulerService routineReminderSchedulerService;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleRoutineCreated(RoutineCreatedEvent event) {
		log.info("루틴 생성 이벤트 처리 시작 - RoutineId: {}", event.getRoutineId());

		try {
			routineService.getUserRoutine(event.getUser(), event.getRoutineId())
				.ifPresent(routine -> {
					LocalDateTime currentDateTime = LocalDateTime.now();
					routineReminderSchedulerService.scheduleRoutineReminders(routine, currentDateTime);
				});
		} catch (Exception e) {
			log.error("루틴 생성 이벤트 처리 중 오류 발생 - RoutineId: {}", event.getRoutineId(), e);
		}
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleRoutineUpdated(RoutineUpdatedEvent event) {
		log.info("루틴 수정 이벤트 처리 시작 - RoutineId: {}", event.getRoutineId());

		try {
			if (event.isReminderRelatedChanged()) {
				// 오늘부터의 알림을 모두 취소
				routineReminderSchedulerService.cancelFutureRoutineReminders(
					event.getRoutineId(), LocalDate.now()
				);

				// 새로운 설정으로 알림 재등록
				routineService.getUserRoutine(event.getUser(), event.getRoutineId())
					.ifPresent(routine -> {
						LocalDateTime currentDateTime = LocalDateTime.now();
						routineReminderSchedulerService.scheduleRoutineReminders(routine, currentDateTime);
					});
			}
		} catch (Exception e) {
			log.error("루틴 수정 이벤트 처리 중 오류 발생 - RoutineId: {}", event.getRoutineId(), e);
		}
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleRoutineDeleted(RoutineDeletedEvent event) {
		log.info("루틴 삭제 이벤트 처리 시작 - RoutineId: {}", event.getRoutineId());

		try {
			routineReminderSchedulerService.cancelAllRoutineReminders(event.getRoutineId());
		} catch (Exception e) {
			log.error("루틴 삭제 이벤트 처리 중 오류 발생 - RoutineId: {}", event.getRoutineId(), e);
		}
	}
}
