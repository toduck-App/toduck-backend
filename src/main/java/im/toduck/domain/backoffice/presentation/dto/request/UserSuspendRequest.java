package im.toduck.domain.backoffice.presentation.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSuspendRequest {

	@NotNull(message = "정지 해제일을 입력해주세요.")
	@Future(message = "정지 해제일은 현재 시간보다 미래여야 합니다.")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime suspendedUntil;

	@NotNull(message = "정지 사유를 입력해주세요.")
	private String suspensionReason;

	@Builder
	private UserSuspendRequest(LocalDateTime suspendedUntil, String suspensionReason) {
		this.suspendedUntil = suspendedUntil;
		this.suspensionReason = suspensionReason;
	}
}
