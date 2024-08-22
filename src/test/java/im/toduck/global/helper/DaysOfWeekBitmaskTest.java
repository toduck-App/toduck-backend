package im.toduck.global.helper;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class DaysOfWeekBitmaskTest {

	@Test
	void 요일_배열로_객체를_생성할_때_지정된_요일만_포함해야_한다() {
		// given
		DayOfWeek[] weekdays = {DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY};

		// when
		DaysOfWeekBitmask bitmask = DaysOfWeekBitmask.of(weekdays);

		// then
		assertSoftly(softly -> {
			softly.assertThat(bitmask.includesDay(DayOfWeek.MONDAY)).isTrue();
			softly.assertThat(bitmask.includesDay(DayOfWeek.TUESDAY)).isFalse();
			softly.assertThat(bitmask.includesDay(DayOfWeek.WEDNESDAY)).isTrue();
			softly.assertThat(bitmask.includesDay(DayOfWeek.THURSDAY)).isFalse();
			softly.assertThat(bitmask.includesDay(DayOfWeek.FRIDAY)).isTrue();
			softly.assertThat(bitmask.includesDay(DayOfWeek.SATURDAY)).isFalse();
			softly.assertThat(bitmask.includesDay(DayOfWeek.SUNDAY)).isFalse();
		});
	}

	@Test
	void 특정_요일_포함_여부를_확인할_때_정확한_결과를_반환해야_한다() {
		// given
		DaysOfWeekBitmask weekendBitmask = DaysOfWeekBitmask.of(new DayOfWeek[] {DayOfWeek.SATURDAY, DayOfWeek.SUNDAY});

		// when & then
		assertSoftly(softly -> {
			softly.assertThat(weekendBitmask.includesDay(DayOfWeek.SATURDAY)).isTrue();
			softly.assertThat(weekendBitmask.includesDay(DayOfWeek.SUNDAY)).isTrue();
			softly.assertThat(weekendBitmask.includesDay(DayOfWeek.MONDAY)).isFalse();
		});
	}

	@Test
	void 날짜와_시간_객체로_요일_포함_여부를_확인할_때_올바른_결과를_제공해야_한다() {
		// given
		DaysOfWeekBitmask weekdaysBitmask = DaysOfWeekBitmask.of(new DayOfWeek[] {
			DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
		});
		LocalDate monday = LocalDate.of(2023, 6, 5);  // 2023-06-05는 월요일
		LocalDateTime saturday = LocalDateTime.of(2023, 6, 10, 12, 0);  // 2023-06-10 12:00는 토요일

		// when & then
		assertSoftly(softly -> {
			softly.assertThat(weekdaysBitmask.includesDayOf(monday)).isTrue();
			softly.assertThat(weekdaysBitmask.includesDayOf(saturday)).isFalse();
		});
	}

	@Test
	void 요일_집합을_요청할_때_정확한_요일_목록을_반환해야_한다() {
		// given
		DaysOfWeekBitmask weekendBitmask = DaysOfWeekBitmask.of(new DayOfWeek[] {DayOfWeek.SATURDAY, DayOfWeek.SUNDAY});

		// when
		Set<DayOfWeek> weekendDays = weekendBitmask.getDaysOfWeek();

		// then
		assertThat(weekendDays).containsExactlyInAnyOrder(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
	}

	@Test
	void 빈_배열로_객체를_생성하려_할_때_예외가_발생해야_한다() {
		// given
		ThrowingCallable code = () -> DaysOfWeekBitmask.of(new DayOfWeek[] {});

		// when & then
		assertThatThrownBy(code)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Invalid bitmask");
	}

	@Test
	void 객체를_문자열로_변환할_때_포함된_요일만_표시되어야_한다() {
		// given
		DaysOfWeekBitmask weekdaysBitmask = DaysOfWeekBitmask.of(new DayOfWeek[] {
			DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY
		});

		// when
		String result = weekdaysBitmask.toString();

		// then
		assertThat(result).contains("MONDAY", "WEDNESDAY", "FRIDAY")
			.doesNotContain("TUESDAY", "THURSDAY", "SATURDAY", "SUNDAY");
	}

	@Test
	void 모든_요일을_포함하여_객체를_생성할_때_모든_요일이_포함되어야_한다() {
		// given
		DaysOfWeekBitmask allDaysBitmask = DaysOfWeekBitmask.of(DayOfWeek.values());

		// when
		Set<DayOfWeek> allDays = allDaysBitmask.getDaysOfWeek();

		// then
		assertThat(allDays).containsExactlyInAnyOrder(DayOfWeek.values());
	}

	@Test
	void 중복된_요일을_포함하여_객체를_생성할_때_중복이_제거되어야_한다() {
		// given
		DayOfWeek[] daysWithDuplicates = {DayOfWeek.MONDAY, DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.WEDNESDAY,
			DayOfWeek.FRIDAY};

		// when
		DaysOfWeekBitmask bitmask = DaysOfWeekBitmask.of(daysWithDuplicates);

		// then
		assertThat(bitmask.getDaysOfWeek()).containsExactlyInAnyOrder(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY,
			DayOfWeek.FRIDAY);
	}
}
