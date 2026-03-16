package im.toduck.domain.schedule.persistence.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ScheduleAlramTest {

    @Nested
    @DisplayName("getMinutes 메서드")
    class GetMinutesTest {

        @Test
        @DisplayName("TEN_MINUTE은 10분을 반환한다")
        void tenMinuteReturns10() {
            // given & when
            int minutes = ScheduleAlram.TEN_MINUTE.getMinutes();

            // then
            assertThat(minutes).isEqualTo(10);
        }

        @Test
        @DisplayName("THIRTY_MINUTE은 30분을 반환한다")
        void thirtyMinuteReturns30() {
            // given & when
            int minutes = ScheduleAlram.THIRTY_MINUTE.getMinutes();

            // then
            assertThat(minutes).isEqualTo(30);
        }

        @Test
        @DisplayName("ONE_DAY는 1440분(24시간)을 반환한다")
        void oneDayReturns1440() {
            // given & when
            int minutes = ScheduleAlram.ONE_DAY.getMinutes();

            // then
            assertThat(minutes).isEqualTo(1440);
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValuesTest {

        @Test
        @DisplayName("ScheduleAlram은 3개의 enum 값을 가진다")
        void hasThreeValues() {
            // given & when
            ScheduleAlram[] values = ScheduleAlram.values();

            // then
            assertThat(values).hasSize(3);
        }

        @Test
        @DisplayName("enum 이름으로 값을 찾을 수 있다")
        void canFindByName() {
            // given & when & then
            assertThat(ScheduleAlram.valueOf("TEN_MINUTE")).isEqualTo(ScheduleAlram.TEN_MINUTE);
            assertThat(ScheduleAlram.valueOf("THIRTY_MINUTE")).isEqualTo(ScheduleAlram.THIRTY_MINUTE);
            assertThat(ScheduleAlram.valueOf("ONE_DAY")).isEqualTo(ScheduleAlram.ONE_DAY);
        }
    }
}
