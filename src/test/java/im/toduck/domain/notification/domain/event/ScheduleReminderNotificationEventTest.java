package im.toduck.domain.notification.domain.event;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import im.toduck.domain.notification.persistence.entity.NotificationType;
import im.toduck.domain.schedule.persistence.vo.ScheduleAlram;

class ScheduleReminderNotificationEventTest {

    @Nested
    @DisplayName("of 팩토리 메서드")
    class OfFactoryMethodTest {

        @Test
        @DisplayName("일반 일정 알림 이벤트를 생성한다")
        void createNormalScheduleReminderEvent() {
            // given
            Long userId = 1L;
            Long scheduleId = 10L;
            String title = "회의";
            ScheduleAlram reminderType = ScheduleAlram.TEN_MINUTE;

            // when
            ScheduleReminderNotificationEvent event = ScheduleReminderNotificationEvent.of(
                    userId, scheduleId, title, reminderType, false);

            // then
            assertThat(event.getUserId()).isEqualTo(userId);
            assertThat(event.getType()).isEqualTo(NotificationType.SCHEDULE_REMINDER);
            assertThat(event.getData().getScheduleId()).isEqualTo(scheduleId);
            assertThat(event.getData().getScheduleTitle()).isEqualTo(title);
            assertThat(event.getData().getReminderType()).isEqualTo(reminderType);
            assertThat(event.getData().isAllDay()).isFalse();
        }

        @Test
        @DisplayName("종일 일정 알림 이벤트를 생성한다")
        void createAllDayScheduleReminderEvent() {
            // given
            Long userId = 2L;
            Long scheduleId = 20L;
            String title = "출장";
            ScheduleAlram reminderType = ScheduleAlram.ONE_DAY;

            // when
            ScheduleReminderNotificationEvent event = ScheduleReminderNotificationEvent.of(
                    userId, scheduleId, title, reminderType, true);

            // then
            assertThat(event.getData().isAllDay()).isTrue();
            assertThat(event.getData().getReminderType()).isEqualTo(ScheduleAlram.ONE_DAY);
        }
    }

    @Nested
    @DisplayName("getPushTitle 메서드")
    class GetPushTitleTest {

        @Test
        @DisplayName("일정 제목을 푸시 제목으로 반환한다")
        void returnScheduleTitleAsPushTitle() {
            // given
            ScheduleReminderNotificationEvent event = ScheduleReminderNotificationEvent.of(
                    1L, 10L, "팀 미팅", ScheduleAlram.THIRTY_MINUTE, false);

            // when
            String pushTitle = event.getPushTitle();

            // then
            assertThat(pushTitle).isEqualTo("팀 미팅");
        }
    }

    @Nested
    @DisplayName("getPushBody 메서드")
    class GetPushBodyTest {

        @Test
        @DisplayName("일반 일정은 알림 분을 포함한 메시지를 반환한다")
        void returnMinutesMessageForNormalSchedule() {
            // given
            ScheduleReminderNotificationEvent event = ScheduleReminderNotificationEvent.of(
                    1L, 10L, "미팅", ScheduleAlram.TEN_MINUTE, false);

            // when
            String pushBody = event.getPushBody();

            // then
            assertThat(pushBody).contains("10분 전");
        }

        @Test
        @DisplayName("30분 알림은 30분 전 메시지를 반환한다")
        void returnThirtyMinutesMessage() {
            // given
            ScheduleReminderNotificationEvent event = ScheduleReminderNotificationEvent.of(
                    1L, 10L, "미팅", ScheduleAlram.THIRTY_MINUTE, false);

            // when
            String pushBody = event.getPushBody();

            // then
            assertThat(pushBody).contains("30분 전");
        }

        @Test
        @DisplayName("종일 일정은 하루 전 메시지를 반환한다")
        void returnOneDayMessageForAllDaySchedule() {
            // given
            ScheduleReminderNotificationEvent event = ScheduleReminderNotificationEvent.of(
                    1L, 10L, "출장", ScheduleAlram.ONE_DAY, true);

            // when
            String pushBody = event.getPushBody();

            // then
            assertThat(pushBody).contains("하루 전");
        }
    }

    @Nested
    @DisplayName("getActionUrl 메서드")
    class GetActionUrlTest {

        @Test
        @DisplayName("toduck://todo URL을 반환한다")
        void returnTodoUrl() {
            // given
            ScheduleReminderNotificationEvent event = ScheduleReminderNotificationEvent.of(
                    1L, 10L, "미팅", ScheduleAlram.TEN_MINUTE, false);

            // when
            String actionUrl = event.getActionUrl();

            // then
            assertThat(actionUrl).isEqualTo("toduck://todo");
        }
    }
}
