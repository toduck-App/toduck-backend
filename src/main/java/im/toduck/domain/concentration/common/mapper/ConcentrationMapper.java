package im.toduck.domain.concentration.common.mapper;

import java.util.List;

import im.toduck.domain.concentration.persistence.entity.Concentration;
import im.toduck.domain.concentration.presentation.dto.request.ConcentrationRequest;
import im.toduck.domain.concentration.presentation.dto.response.ConcentrationListResponse;
import im.toduck.domain.concentration.presentation.dto.response.ConcentrationResponse;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

	public static Concentration concentration(User user, ConcentrationRequest request) {
		return Concentration.builder()
			.user(user)
			.date(request.date())
			.build();
	}

	public static ConcentrationListResponse toListConcentrationResponse(List<Concentration> concentrations) {
		return ConcentrationListResponse.from(concentrations);
	}
}
