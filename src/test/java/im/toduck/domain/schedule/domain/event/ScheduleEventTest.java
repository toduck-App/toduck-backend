package im.toduck.domain.schedule.domain.event;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ScheduleEventTest {

    @Nested
    @DisplayName("ScheduleCreatedEvent")
    class ScheduleCreatedEventTest {

        @Test
        @DisplayName("일정 ID와 사용자 ID를 포함하여 생성된다")
        void createWithScheduleIdAndUserId() {
            // given
            Long scheduleId = 1L;
            Long userId = 2L;

            // when
            ScheduleCreatedEvent event = new ScheduleCreatedEvent(scheduleId, userId);

            // then
            assertThat(event.getScheduleId()).isEqualTo(scheduleId);
            assertThat(event.getUserId()).isEqualTo(userId);
        }
    }

    @Nested
    @DisplayName("ScheduleUpdatedEvent")
    class ScheduleUpdatedEventTest {

        @Test
        @DisplayName("알림 설정이 변경되면 isReminderRelatedChanged가 true를 반환한다")
        void returnTrueWhenAlarmChanged() {
            // given
            ScheduleUpdatedEvent event = new ScheduleUpdatedEvent(
                    1L, 1L, true, false, false, false, false, false);

            // when & then
            assertThat(event.isReminderRelatedChanged()).isTrue();
        }

        @Test
        @DisplayName("시간이 변경되면 isReminderRelatedChanged가 true를 반환한다")
        void returnTrueWhenTimeChanged() {
            // given
            ScheduleUpdatedEvent event = new ScheduleUpdatedEvent(
                    1L, 1L, false, true, false, false, false, false);

            // when & then
            assertThat(event.isReminderRelatedChanged()).isTrue();
        }

        @Test
        @DisplayName("종일 여부가 변경되면 isReminderRelatedChanged가 true를 반환한다")
        void returnTrueWhenAllDayChanged() {
            // given
            ScheduleUpdatedEvent event = new ScheduleUpdatedEvent(
                    1L, 1L, false, false, true, false, false, false);

            // when & then
            assertThat(event.isReminderRelatedChanged()).isTrue();
        }

        @Test
        @DisplayName("제목이 변경되면 isReminderRelatedChanged가 true를 반환한다")
        void returnTrueWhenTitleChanged() {
            // given
            ScheduleUpdatedEvent event = new ScheduleUpdatedEvent(
                    1L, 1L, false, false, false, true, false, false);

            // when & then
            assertThat(event.isReminderRelatedChanged()).isTrue();
        }

        @Test
        @DisplayName("날짜가 변경되면 isReminderRelatedChanged가 true를 반환한다")
        void returnTrueWhenDateChanged() {
            // given
            ScheduleUpdatedEvent event = new ScheduleUpdatedEvent(
                    1L, 1L, false, false, false, false, true, false);

            // when & then
            assertThat(event.isReminderRelatedChanged()).isTrue();
        }

        @Test
        @DisplayName("반복 요일이 변경되면 isReminderRelatedChanged가 true를 반환한다")
        void returnTrueWhenDaysOfWeekChanged() {
            // given
            ScheduleUpdatedEvent event = new ScheduleUpdatedEvent(
                    1L, 1L, false, false, false, false, false, true);

            // when & then
            assertThat(event.isReminderRelatedChanged()).isTrue();
        }

        @Test
        @DisplayName("아무것도 변경되지 않으면 isReminderRelatedChanged가 false를 반환한다")
        void returnFalseWhenNothingChanged() {
            // given
            ScheduleUpdatedEvent event = new ScheduleUpdatedEvent(
                    1L, 1L, false, false, false, false, false, false);

            // when & then
            assertThat(event.isReminderRelatedChanged()).isFalse();
        }
    }

    @Nested
    @DisplayName("ScheduleDeletedEvent")
    class ScheduleDeletedEventTest {

        @Test
        @DisplayName("일정 ID를 포함하여 생성된다")
        void createWithScheduleId() {
            // given
            Long scheduleId = 1L;

            // when
            ScheduleDeletedEvent event = new ScheduleDeletedEvent(scheduleId);

            // then
            assertThat(event.getScheduleId()).isEqualTo(scheduleId);
        }
    }
}
