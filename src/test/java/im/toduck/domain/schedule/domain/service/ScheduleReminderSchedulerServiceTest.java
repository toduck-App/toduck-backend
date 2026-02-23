package im.toduck.domain.schedule.domain.service;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.test.util.ReflectionTestUtils;

import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.domain.routine.persistence.vo.PlanCategoryColor;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleReminderJob;
import im.toduck.domain.schedule.persistence.repository.ScheduleReminderJobRepository;
import im.toduck.domain.schedule.persistence.vo.ScheduleAlram;
import im.toduck.domain.schedule.persistence.vo.ScheduleDate;
import im.toduck.domain.schedule.persistence.vo.ScheduleTime;
import im.toduck.domain.user.persistence.entity.User;

@ExtendWith(MockitoExtension.class)
class ScheduleReminderSchedulerServiceTest {

        @InjectMocks
        private ScheduleReminderSchedulerService schedulerService;

        @Mock
        private Scheduler scheduler;

        @Mock
        private ScheduleReminderJobRepository scheduleReminderJobRepository;

        private User testUser;

        @BeforeEach
        void setUp() {
                testUser = GENERAL_USER();
                ReflectionTestUtils.setField(testUser, "id", 1L);
        }

        private Schedule createSchedule(
                        final Long id,
                        final Boolean isAllDay,
                        final LocalTime time,
                        final ScheduleAlram alarm,
                        final LocalDate startDate,
                        final LocalDate endDate) {
                Schedule schedule = Schedule.builder()
                                .user(testUser)
                                .title("테스트 일정")
                                .category(PlanCategory.COMPUTER)
                                .color(PlanCategoryColor.from("#FF5733"))
                                .scheduleDate(ScheduleDate.of(startDate, endDate))
                                .scheduleTime(ScheduleTime.of(isAllDay, time, alarm))
                                .build();

                ReflectionTestUtils.setField(schedule, "id", id);
                return schedule;
        }

        @Nested
        @DisplayName("scheduleScheduleReminders 메서드")
        class ScheduleScheduleRemindersTest {

                @Test
                @DisplayName("알림이 null이면 스케줄링을 스킵한다")
                void skipWhenAlarmIsNull() throws SchedulerException {
                        // given
                        Schedule schedule = createSchedule(
                                        1L, false, LocalTime.of(14, 0), null,
                                        LocalDate.of(2026, 2, 24), LocalDate.of(2026, 2, 24));
                        LocalDateTime currentDateTime = LocalDateTime.of(2026, 2, 23, 12, 0);

                        // when
                        schedulerService.scheduleScheduleReminders(schedule, currentDateTime, false);

                        // then
                        then(scheduler).shouldHaveNoInteractions();
                        then(scheduleReminderJobRepository).shouldHaveNoInteractions();
                }

                @Test
                @DisplayName("일반 일정에 TEN_MINUTE 알림이 설정된 경우 10분 전에 알림을 스케줄링한다")
                void scheduleReminderForNormalScheduleWithTenMinute() throws SchedulerException {
                        // given
                        LocalDate scheduleDate = LocalDate.of(2026, 2, 24);
                        Schedule schedule = createSchedule(
                                        1L, false, LocalTime.of(14, 0), ScheduleAlram.TEN_MINUTE,
                                        scheduleDate, scheduleDate);
                        LocalDateTime currentDateTime = LocalDateTime.of(2026, 2, 23, 12, 0);

                        given(scheduleReminderJobRepository.existsByScheduleIdAndReminderDateAndReminderTime(
                                        anyLong(), any(LocalDate.class), any(LocalTime.class))).willReturn(false);

                        given(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                                        .willReturn(null);

                        // when
                        schedulerService.scheduleScheduleReminders(schedule, currentDateTime, false);

                        // then
                        then(scheduler).should(atLeastOnce()).scheduleJob(any(JobDetail.class), any(Trigger.class));
                        then(scheduleReminderJobRepository).should(atLeastOnce()).save(any(ScheduleReminderJob.class));
                }

                @Test
                @DisplayName("일반 일정에 THIRTY_MINUTE 알림이 설정된 경우 30분 전에 알림을 스케줄링한다")
                void scheduleReminderForNormalScheduleWithThirtyMinute() throws SchedulerException {
                        // given
                        LocalDate scheduleDate = LocalDate.of(2026, 2, 24);
                        Schedule schedule = createSchedule(
                                        2L, false, LocalTime.of(14, 0), ScheduleAlram.THIRTY_MINUTE,
                                        scheduleDate, scheduleDate);
                        LocalDateTime currentDateTime = LocalDateTime.of(2026, 2, 23, 12, 0);

                        given(scheduleReminderJobRepository.existsByScheduleIdAndReminderDateAndReminderTime(
                                        anyLong(), any(LocalDate.class), any(LocalTime.class))).willReturn(false);

                        given(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                                        .willReturn(null);

                        // when
                        schedulerService.scheduleScheduleReminders(schedule, currentDateTime, false);

                        // then
                        then(scheduler).should(atLeastOnce()).scheduleJob(any(JobDetail.class), any(Trigger.class));
                        then(scheduleReminderJobRepository).should(atLeastOnce()).save(any(ScheduleReminderJob.class));
                }

                @Test
                @DisplayName("종일 일정에 ONE_DAY 알림이 설정된 경우 하루 전 10시에 알림을 스케줄링한다")
                void scheduleReminderForAllDaySchedule() throws SchedulerException {
                        // given
                        LocalDate scheduleDate = LocalDate.of(2026, 2, 24);
                        Schedule schedule = createSchedule(
                                        3L, true, null, ScheduleAlram.ONE_DAY,
                                        scheduleDate, scheduleDate);
                        // 알림 시간: 2026-02-23 10:00 (하루 전 10시)
                        LocalDateTime currentDateTime = LocalDateTime.of(2026, 2, 23, 5, 0);

                        given(scheduleReminderJobRepository.existsByScheduleIdAndReminderDateAndReminderTime(
                                        anyLong(), any(LocalDate.class), any(LocalTime.class))).willReturn(false);

                        given(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                                        .willReturn(null);

                        // when
                        schedulerService.scheduleScheduleReminders(schedule, currentDateTime, false);

                        // then
                        then(scheduler).should(atLeastOnce()).scheduleJob(any(JobDetail.class), any(Trigger.class));
                        then(scheduleReminderJobRepository).should(atLeastOnce()).save(any(ScheduleReminderJob.class));
                }

                @Test
                @DisplayName("이미 스케줄링된 알림은 중복 스케줄링하지 않는다")
                void skipDuplicateScheduling() throws SchedulerException {
                        // given
                        LocalDate scheduleDate = LocalDate.of(2026, 2, 24);
                        Schedule schedule = createSchedule(
                                        4L, false, LocalTime.of(14, 0), ScheduleAlram.TEN_MINUTE,
                                        scheduleDate, scheduleDate);
                        LocalDateTime currentDateTime = LocalDateTime.of(2026, 2, 23, 12, 0);

                        given(scheduleReminderJobRepository.existsByScheduleIdAndReminderDateAndReminderTime(
                                        anyLong(), any(LocalDate.class), any(LocalTime.class))).willReturn(true);

                        // when
                        schedulerService.scheduleScheduleReminders(schedule, currentDateTime, false);

                        // then
                        then(scheduler).shouldHaveNoInteractions();
                }

                @Test
                @DisplayName("이미 지나간 알림 시간은 스케줄링하지 않는다")
                void skipPastReminderTime() throws SchedulerException {
                        // given
                        LocalDate scheduleDate = LocalDate.of(2026, 2, 23);
                        Schedule schedule = createSchedule(
                                        5L, false, LocalTime.of(10, 0), ScheduleAlram.TEN_MINUTE,
                                        scheduleDate, scheduleDate);
                        // 알림 시간: 09:50, 현재 시간: 12:00 → 이미 지남
                        LocalDateTime currentDateTime = LocalDateTime.of(2026, 2, 23, 12, 0);

                        // when
                        schedulerService.scheduleScheduleReminders(schedule, currentDateTime, false);

                        // then
                        then(scheduler).shouldHaveNoInteractions();
                        then(scheduleReminderJobRepository).should(never()).save(any());
                }

                @Test
                @DisplayName("시작일이 지나간 기간 일정은 현재 날짜부터 스케줄링한다")
                void scheduleFromCurrentDateForPastStartDate() throws SchedulerException {
                        // given
                        Schedule schedule = createSchedule(
                                        6L, false, LocalTime.of(14, 0), ScheduleAlram.TEN_MINUTE,
                                        LocalDate.of(2026, 2, 20), LocalDate.of(2026, 2, 28));
                        LocalDateTime currentDateTime = LocalDateTime.of(2026, 2, 23, 12, 0);

                        given(scheduleReminderJobRepository.existsByScheduleIdAndReminderDateAndReminderTime(
                                        anyLong(), any(LocalDate.class), any(LocalTime.class))).willReturn(false);

                        given(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                                        .willReturn(null);

                        // when
                        schedulerService.scheduleScheduleReminders(schedule, currentDateTime, false);

                        // then
                        then(scheduler).should(atLeastOnce()).scheduleJob(any(JobDetail.class), any(Trigger.class));
                }
        }

        @Nested
        @DisplayName("cancelAllScheduleReminders 메서드")
        class CancelAllScheduleRemindersTest {

                @Test
                @DisplayName("일정의 모든 알림을 취소한다")
                void cancelAllReminders() throws SchedulerException {
                        // given
                        Long scheduleId = 1L;

                        ScheduleReminderJob job = ScheduleReminderJob.builder()
                                        .scheduleId(scheduleId)
                                        .userId(1L)
                                        .reminderDate(LocalDate.of(2026, 2, 24))
                                        .reminderTime(LocalTime.of(13, 50))
                                        .jobKey("schedule_1_2026-02-24_1350")
                                        .build();

                        given(scheduleReminderJobRepository.findByScheduleId(scheduleId))
                                        .willReturn(java.util.List.of(job));
                        given(scheduler.checkExists(any(org.quartz.JobKey.class))).willReturn(true);
                        given(scheduler.deleteJob(any(org.quartz.JobKey.class))).willReturn(true);

                        // when
                        schedulerService.cancelAllScheduleReminders(scheduleId);

                        // then
                        then(scheduler).should().deleteJob(any(org.quartz.JobKey.class));
                        then(scheduleReminderJobRepository).should().deleteByScheduleId(scheduleId);
                }
        }

        @Nested
        @DisplayName("cancelFutureScheduleReminders 메서드")
        class CancelFutureScheduleRemindersTest {

                @Test
                @DisplayName("특정 날짜 이후의 알림을 취소한다")
                void cancelFutureReminders() throws SchedulerException {
                        // given
                        Long scheduleId = 1L;
                        LocalDate fromDate = LocalDate.of(2026, 2, 25);

                        ScheduleReminderJob job = ScheduleReminderJob.builder()
                                        .scheduleId(scheduleId)
                                        .userId(1L)
                                        .reminderDate(LocalDate.of(2026, 2, 26))
                                        .reminderTime(LocalTime.of(13, 50))
                                        .jobKey("schedule_1_2026-02-26_1350")
                                        .build();

                        given(scheduleReminderJobRepository.findByScheduleIdAndReminderDateGreaterThanEqual(
                                        scheduleId, fromDate))
                                        .willReturn(java.util.List.of(job));
                        given(scheduler.checkExists(any(org.quartz.JobKey.class))).willReturn(true);
                        given(scheduler.deleteJob(any(org.quartz.JobKey.class))).willReturn(true);

                        // when
                        schedulerService.cancelFutureScheduleReminders(scheduleId, fromDate);

                        // then
                        then(scheduler).should().deleteJob(any(org.quartz.JobKey.class));
                        then(scheduleReminderJobRepository).should()
                                        .deleteByScheduleIdAndReminderDateAfter(scheduleId, fromDate);
                }
        }
}
