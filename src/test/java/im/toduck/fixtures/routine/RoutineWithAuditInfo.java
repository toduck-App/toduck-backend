package im.toduck.fixtures.routine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import im.toduck.domain.routine.persistence.entity.Routine;

public class RoutineWithAuditInfo {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final Routine routine;
	private final LocalDateTime createdAt;
	private final LocalDateTime scheduleModifiedAt;
	private final LocalDateTime deletedAt;

	private RoutineWithAuditInfo(final Routine routine, LocalDateTime createdAt,
		LocalDateTime scheduleModifiedAt, LocalDateTime deletedAt) {
		this.routine = routine;
		this.createdAt = createdAt;
		this.scheduleModifiedAt = scheduleModifiedAt;
		this.deletedAt = deletedAt;
	}

	public static RoutineWithAuditInfoBuilder builder() {
		return new RoutineWithAuditInfoBuilder();
	}

	public Routine getRoutine() {
		return routine;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getScheduleModifiedAt() {
		return scheduleModifiedAt;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	public boolean requiresAudit() {
		return createdAt != null || scheduleModifiedAt != null || deletedAt != null;
	}

	public static class RoutineWithAuditInfoBuilder {
		private Routine routine;
		private LocalDateTime createdAt;
		private LocalDateTime scheduleModifiedAt;
		private LocalDateTime deletedAt;

		private RoutineWithAuditInfoBuilder() {
		}

		public RoutineWithAuditInfoBuilder routine(Routine routine) {
			this.routine = routine;
			return this;
		}

		/**
		 * 루틴의 생성 시간을 설정합니다.
		 * schedule_modified_at이 별도로 지정되지 않은 경우, 이 시간으로 자동 설정됩니다.
		 *
		 * @param dateTime 'yyyy-MM-dd HH:mm:ss' 형식의 날짜와 시간 문자열
		 *                 예시: "2024-01-01 13:30:00"
		 * @throws DateTimeParseException 날짜와 시간 문자열을 파싱할 수 없는 경우
		 */
		public RoutineWithAuditInfoBuilder createdAt(String dateTime) {
			this.createdAt = LocalDateTime.parse(dateTime, FORMATTER);
			return this;
		}

		/**
		 * 루틴의 스케줄 수정 시간을 설정합니다.
		 * 이 값을 설정하지 않으면 createdAt과 동일한 시간으로 설정됩니다.
		 *
		 * @param dateTime 'yyyy-MM-dd HH:mm:ss' 형식의 날짜와 시간 문자열
		 *                 예시: "2024-01-01 13:30:00"
		 * @throws DateTimeParseException 날짜와 시간 문자열을 파싱할 수 없는 경우
		 */
		public RoutineWithAuditInfoBuilder scheduleModifiedAt(String dateTime) {
			this.scheduleModifiedAt = LocalDateTime.parse(dateTime, FORMATTER);
			return this;
		}

		/**
		 * 루틴의 삭제 시간을 설정합니다.
		 *
		 * @param dateTime 'yyyy-MM-dd HH:mm:ss' 형식의 날짜와 시간 문자열
		 *                 예시: "2024-01-01 13:30:00"
		 * @throws DateTimeParseException 날짜와 시간 문자열을 파싱할 수 없는 경우
		 */
		public RoutineWithAuditInfoBuilder deletedAt(String dateTime) {
			this.deletedAt = LocalDateTime.parse(dateTime, FORMATTER);
			return this;
		}

		/**
		 * RoutineWithAuditInfo 객체를 생성합니다.
		 *
		 * 다음과 같은 경우들이 유효합니다:
		 * 1. audit 필드를 전혀 지정하지 않은 경우
		 * 2. createdAt만 지정한 경우: scheduleModifiedAt은 createdAt과 동일하게 설정되고, deletedAt은 null
		 * 3. createdAt과 scheduleModifiedAt을 지정한 경우: deletedAt은 null
		 * 4. createdAt과 deletedAt을 지정한 경우: scheduleModifiedAt은 createdAt과 동일하게 설정
		 * 5. 모든 필드(createdAt, scheduleModifiedAt, deletedAt)를 지정한 경우
		 *
		 * 이외의 모든 조합(예: scheduleModifiedAt만 지정, deletedAt만 지정 등)은 IllegalStateException을 발생시킵니다.
		 *
		 * @return 생성된 RoutineWithAuditInfo 객체
		 * @throws IllegalStateException 유효하지 않은 필드 조합으로 생성을 시도한 경우
		 */
		public RoutineWithAuditInfo build() {
			if (routine == null) {
				throw new IllegalStateException("Routine 은 필수 값입니다.");
			}

			if (createdAt == null && scheduleModifiedAt == null && deletedAt == null) {
				return new RoutineWithAuditInfo(routine, null, null, null);
			}

			if (createdAt != null && scheduleModifiedAt == null && deletedAt == null) {
				return new RoutineWithAuditInfo(routine, createdAt, createdAt, null);
			}

			if (createdAt != null && scheduleModifiedAt != null && deletedAt == null) {
				return new RoutineWithAuditInfo(routine, createdAt, scheduleModifiedAt, null);
			}

			if (createdAt != null && scheduleModifiedAt == null && deletedAt != null) {
				return new RoutineWithAuditInfo(routine, createdAt, createdAt, deletedAt);
			}

			if (createdAt != null && scheduleModifiedAt != null && deletedAt != null) {
				return new RoutineWithAuditInfo(routine, createdAt, scheduleModifiedAt, deletedAt);
			}

			throw new IllegalStateException(
				"유효하지 않은 audit 필드 조합입니다. build() 메서드의 JavaDoc을 참고하세요."
			);
		}
	}
}
