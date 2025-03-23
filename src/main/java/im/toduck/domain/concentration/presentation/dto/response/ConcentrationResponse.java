package im.toduck.domain.concentration.presentation.dto.response;

import java.time.LocalDate;

import im.toduck.domain.concentration.persistence.entity.Concentration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ConcentrationResponse(
	@Schema(description = "집중 ID", example = "1") Long id,
	@Schema(description = "집중 날짜", example = "2025-03-12") LocalDate date,
	@Schema(description = "달성 횟수", example = "2") Integer targetCount,
	@Schema(description = "설정 횟수", example = "5") Integer settingCount,
	@Schema(description = "집중 시간(초)", example = "1200") Integer time,
	@Schema(description = "달성률(%)", example = "40") Integer percentage
) {
	public static ConcentrationResponse fromEntity(Concentration concentration) {
		int percentage = (concentration.getSettingCount() == 0)
			? 0
			: (int)((concentration.getTargetCount() / (double)concentration.getSettingCount()) * 100);

		return ConcentrationResponse.builder()
			.id(concentration.getId())
			.date(concentration.getDate())
			.targetCount(concentration.getTargetCount())
			.settingCount(concentration.getSettingCount())
			.time(concentration.getTime())
			.percentage(percentage)
			.build();
	}
}
