package im.toduck.global.helper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

public class DaysOfWeekBitmask {
	private static final byte MIN_VALID_BITMASK = 0x01; // 00000001
	private static final byte MAX_VALID_BITMASK = 0x7F; // 01111111

	private final byte bitmask;

	public static class DayBitmasks {
		public static final byte MONDAY = (byte)(1 << (DayOfWeek.MONDAY.getValue() - 1));    // 00000001
		public static final byte TUESDAY = (byte)(1 << (DayOfWeek.TUESDAY.getValue() - 1));   // 00000010
		public static final byte WEDNESDAY = (byte)(1 << (DayOfWeek.WEDNESDAY.getValue() - 1)); // 00000100
		public static final byte THURSDAY = (byte)(1 << (DayOfWeek.THURSDAY.getValue() - 1));  // 00001000
		public static final byte FRIDAY = (byte)(1 << (DayOfWeek.FRIDAY.getValue() - 1));    // 00010000
		public static final byte SATURDAY = (byte)(1 << (DayOfWeek.SATURDAY.getValue() - 1));  // 00100000
		public static final byte SUNDAY = (byte)(1 << (DayOfWeek.SUNDAY.getValue() - 1));    // 01000000

		public static final byte WEEKDAYS = (byte)(MONDAY | TUESDAY | WEDNESDAY | THURSDAY | FRIDAY);
		public static final byte WEEKEND = (byte)(SATURDAY | SUNDAY);
		public static final byte ALL_DAYS = (byte)(WEEKDAYS | WEEKEND);
	}

	private DaysOfWeekBitmask(byte bitmask) {
		if (bitmask < MIN_VALID_BITMASK) {
			throw new IllegalArgumentException(
				"Invalid bitmask. Must be between " + MIN_VALID_BITMASK + " and " + MAX_VALID_BITMASK
			);
		}

		this.bitmask = bitmask;
	}

	public static DaysOfWeekBitmask createByDayOfWeek(@NotNull List<DayOfWeek> daysOfWeek) {
		byte bitmask = (byte)daysOfWeek.stream()
			.mapToInt(DaysOfWeekBitmask::getDayBitmask)
			.reduce(0, (a, b) -> a | b);

		return new DaysOfWeekBitmask(bitmask);
	}

	public static DaysOfWeekBitmask from(byte bitmask) {
		return new DaysOfWeekBitmask(bitmask);
	}

	public boolean includesDay(DayOfWeek day) {
		return (bitmask & getDayBitmask(day)) != 0;
	}

	public boolean includesDayOf(LocalDate date) {
		return includesDay(date.getDayOfWeek());
	}

	public boolean includesDayOf(LocalDateTime dateTime) {
		return includesDay(dateTime.getDayOfWeek());
	}

	public Set<DayOfWeek> getDaysOfWeek() {
		return Arrays.stream(DayOfWeek.values())
			.filter(this::includesDay)
			.collect(Collectors.toCollection(() -> EnumSet.noneOf(DayOfWeek.class)));
	}

	public static byte getDayBitmask(DayOfWeek day) {
		return (byte)(1 << (day.getValue() - 1));
	}

	/**
	 * 주어진 시작일과 종료일 사이에서 이 비트마스크에 포함된 요일에 해당하는 날짜들의 스트림을 반환합니다.
	 *
	 * @param startDate 시작일 (포함)
	 * @param endDate 종료일 (포함)
	 * @return 해당 요일에 맞는 날짜들의 스트림
	 */
	public Stream<LocalDate> streamMatchingDatesInRange(LocalDate startDate, LocalDate endDate) {
		if (startDate.isAfter(endDate)) {
			return Stream.empty();
		}

		return startDate.datesUntil(endDate.plusDays(1))
			.filter(this::includesDayOf);
	}

	/**
	 * 시작일부터 종료일까지의 기간에 포함된 모든 요일에 대한 비트마스크를 생성합니다.
	 * 예: 월요일부터 수요일까지의 기간은 0000111 (0x07) 반환
	 * 일주일 이상의 기간은 항상 0111111 (0x7F) 반환
	 *
	 * @param startDate 시작일 (포함)
	 * @param endDate 종료일 (포함)
	 * @return 기간에 포함된 모든 요일에 대한 비트마스크
	 */
	public static DaysOfWeekBitmask createFromDateRange(LocalDate startDate, LocalDate endDate) {
		if (startDate.plusDays(6).isBefore(endDate) || startDate.plusDays(6).isEqual(endDate)) {
			return new DaysOfWeekBitmask(DayBitmasks.ALL_DAYS);
		}

		byte bitmask = 0;
		LocalDate current = startDate;

		while (!current.isAfter(endDate)) {
			bitmask |= getDayBitmask(current.getDayOfWeek());
			current = current.plusDays(1);
		}

		return new DaysOfWeekBitmask(bitmask);
	}

	/**
	 * 시작일부터 종료일까지의 기간에 포함된 모든 요일에 대한 비트마스크 값을 반환합니다.
	 * 내부 구현을 숨기고 바이트 값만 필요한 경우 사용합니다.
	 *
	 * @param startDate 시작일 (포함)
	 * @param endDate 종료일 (포함)
	 * @return 기간에 포함된 모든 요일에 대한 비트마스크 값
	 */
	public static byte getDaysOfWeekBitmaskValueInRange(LocalDate startDate, LocalDate endDate) {
		return createFromDateRange(startDate, endDate).getValue();
	}

	public byte getValue() {
		return bitmask;
	}

	@Override
	public String toString() {
		return getDaysOfWeek().toString();
	}
}
