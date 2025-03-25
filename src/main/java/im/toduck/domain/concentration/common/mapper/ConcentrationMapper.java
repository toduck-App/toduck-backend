package im.toduck.domain.concentration.common.mapper;

import im.toduck.domain.concentration.persistence.entity.Concentration;
import im.toduck.domain.concentration.presentation.dto.response.ConcentrationResponse;

public class ConcentrationMapper {
	public static ConcentrationResponse fromConcentration(Concentration concentration) {
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
