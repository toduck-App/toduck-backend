package im.toduck.domain.notification.domain.data;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import im.toduck.domain.schedule.persistence.vo.ScheduleAlram;

class ScheduleReminderDataTest {

    @Nested
    @DisplayName("of 팩토리 메서드")
    class OfFactoryMethodTest {

        @Test
        @DisplayName("모든 필드가 올바르게 설정된 ScheduleReminderData를 생성한다")
        void createWithAllFields() {
            // given
            Long scheduleId = 1L;
            String title = "팀 미팅";
            ScheduleAlram reminderType = ScheduleAlram.TEN_MINUTE;
            boolean isAllDay = false;

            // when
            ScheduleReminderData data = ScheduleReminderData.of(scheduleId, title, reminderType, isAllDay);

            // then
            assertThat(data.getScheduleId()).isEqualTo(scheduleId);
            assertThat(data.getScheduleTitle()).isEqualTo(title);
            assertThat(data.getReminderType()).isEqualTo(reminderType);
            assertThat(data.isAllDay()).isFalse();
        }

        @Test
        @DisplayName("종일 일정 데이터를 생성한다")
        void createAllDayData() {
            // given
            Long scheduleId = 2L;
            String title = "출장";
            ScheduleAlram reminderType = ScheduleAlram.ONE_DAY;

            // when
            ScheduleReminderData data = ScheduleReminderData.of(scheduleId, title, reminderType, true);

            // then
            assertThat(data.isAllDay()).isTrue();
            assertThat(data.getReminderType()).isEqualTo(ScheduleAlram.ONE_DAY);
        }
    }

    @Nested
    @DisplayName("기본 생성자")
    class NoArgsConstructorTest {

        @Test
        @DisplayName("기본 생성자로 생성 시 모든 필드가 null 또는 기본값이다")
        void createWithNoArgs() {
            // given & when
            ScheduleReminderData data = new ScheduleReminderData();

            // then
            assertThat(data.getScheduleId()).isNull();
            assertThat(data.getScheduleTitle()).isNull();
            assertThat(data.getReminderType()).isNull();
            assertThat(data.isAllDay()).isFalse();
        }
    }
}
