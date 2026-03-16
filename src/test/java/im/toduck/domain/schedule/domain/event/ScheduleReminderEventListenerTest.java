package im.toduck.domain.schedule.domain.event;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.domain.routine.persistence.vo.PlanCategoryColor;
import im.toduck.domain.schedule.domain.service.ScheduleReadService;
import im.toduck.domain.schedule.domain.service.ScheduleReminderSchedulerService;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.vo.ScheduleAlram;
import im.toduck.domain.schedule.persistence.vo.ScheduleDate;
import im.toduck.domain.schedule.persistence.vo.ScheduleTime;
import im.toduck.domain.user.persistence.entity.User;

@ExtendWith(MockitoExtension.class)
class ScheduleReminderEventListenerTest {

    @InjectMocks
    private ScheduleReminderEventListener eventListener;

    @Mock
    private ScheduleReadService scheduleReadService;

    @Mock
    private ScheduleReminderSchedulerService scheduleReminderSchedulerService;

    private Schedule createTestSchedule(final Long id) {
        User user = GENERAL_USER();
        ReflectionTestUtils.setField(user, "id", 1L);

        Schedule schedule = Schedule.builder()
                .user(user)
                .title("테스트 일정")
                .category(PlanCategory.COMPUTER)
                .color(PlanCategoryColor.from("#FF5733"))
                .scheduleDate(ScheduleDate.of(LocalDate.of(2026, 2, 24), LocalDate.of(2026, 2, 24)))
                .scheduleTime(ScheduleTime.of(false, LocalTime.of(14, 0), ScheduleAlram.TEN_MINUTE))
                .build();

        ReflectionTestUtils.setField(schedule, "id", id);
        return schedule;
    }

    @Nested
    @DisplayName("handleScheduleCreated 메서드")
    class HandleScheduleCreatedTest {

        @Test
        @DisplayName("일정 생성 이벤트를 받으면 알림을 스케줄링한다")
        void scheduleReminderOnScheduleCreated() {
            // given
            Long scheduleId = 1L;
            Long userId = 1L;
            ScheduleCreatedEvent event = new ScheduleCreatedEvent(scheduleId, userId);
            Schedule schedule = createTestSchedule(scheduleId);

            given(scheduleReadService.getScheduleById(scheduleId)).willReturn(Optional.of(schedule));

            // when
            eventListener.handleScheduleCreated(event);

            // then
            then(scheduleReminderSchedulerService).should()
                    .scheduleScheduleReminders(eq(schedule), any(), eq(false));
        }

        @Test
        @DisplayName("일정을 찾지 못하면 스케줄링을 하지 않는다")
        void doNotScheduleWhenScheduleNotFound() {
            // given
            Long scheduleId = 999L;
            Long userId = 1L;
            ScheduleCreatedEvent event = new ScheduleCreatedEvent(scheduleId, userId);

            given(scheduleReadService.getScheduleById(scheduleId)).willReturn(Optional.empty());

            // when
            eventListener.handleScheduleCreated(event);

            // then
            then(scheduleReminderSchedulerService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("예외가 발생해도 이벤트 처리는 실패하지 않는다")
        void doNotFailOnException() {
            // given
            Long scheduleId = 1L;
            Long userId = 1L;
            ScheduleCreatedEvent event = new ScheduleCreatedEvent(scheduleId, userId);

            given(scheduleReadService.getScheduleById(scheduleId))
                    .willThrow(new RuntimeException("DB 오류"));

            // when & then (예외가 전파되지 않음)
            org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                    () -> eventListener.handleScheduleCreated(event));
        }
    }

    @Nested
    @DisplayName("handleScheduleUpdated 메서드")
    class HandleScheduleUpdatedTest {

        @Test
        @DisplayName("알림 관련 필드가 변경되면 기존 알림을 취소하고 재스케줄링한다")
        void rescheduleOnReminderRelatedChange() {
            // given
            Long scheduleId = 1L;
            Long userId = 1L;
            ScheduleUpdatedEvent event = new ScheduleUpdatedEvent(
                    scheduleId, userId, true, false, false, false, false, false);
            Schedule schedule = createTestSchedule(scheduleId);

            given(scheduleReadService.getScheduleById(scheduleId)).willReturn(Optional.of(schedule));

            // when
            eventListener.handleScheduleUpdated(event);

            // then
            then(scheduleReminderSchedulerService).should()
                    .cancelFutureScheduleReminders(eq(scheduleId), any(LocalDate.class));
            then(scheduleReminderSchedulerService).should()
                    .scheduleScheduleReminders(eq(schedule), any(), eq(false));
        }

        @Test
        @DisplayName("알림 관련 필드가 변경되지 않으면 아무 작업도 하지 않는다")
        void doNothingOnNonReminderChange() {
            // given
            Long scheduleId = 1L;
            Long userId = 1L;
            ScheduleUpdatedEvent event = new ScheduleUpdatedEvent(
                    scheduleId, userId, false, false, false, false, false, false);

            // when
            eventListener.handleScheduleUpdated(event);

            // then
            then(scheduleReminderSchedulerService).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("handleScheduleDeleted 메서드")
    class HandleScheduleDeletedTest {

        @Test
        @DisplayName("일정 삭제 이벤트를 받으면 모든 알림을 취소한다")
        void cancelAllRemindersOnDelete() {
            // given
            Long scheduleId = 1L;
            ScheduleDeletedEvent event = new ScheduleDeletedEvent(scheduleId);

            // when
            eventListener.handleScheduleDeleted(event);

            // then
            then(scheduleReminderSchedulerService).should()
                    .cancelAllScheduleReminders(scheduleId);
        }

        @Test
        @DisplayName("예외가 발생해도 이벤트 처리는 실패하지 않는다")
        void doNotFailOnException() {
            // given
            Long scheduleId = 1L;
            ScheduleDeletedEvent event = new ScheduleDeletedEvent(scheduleId);

            willThrow(new RuntimeException("스케줄러 오류"))
                    .given(scheduleReminderSchedulerService)
                    .cancelAllScheduleReminders(scheduleId);

            // when & then
            org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                    () -> eventListener.handleScheduleDeleted(event));
        }
    }
}
