package im.toduck.domain.social.common.mapper;

import im.toduck.domain.social.persistence.entity.Report;
import im.toduck.domain.social.persistence.entity.ReportType;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.presentation.dto.response.ReportCreateResponse;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportMapper {
	public static Report toReport(User user, Social social, ReportType reportType, String reason) {
		return Report.builder()
			.user(user)
			.social(social)
			.reportType(reportType)
			.reason(reportType == ReportType.OTHER ? reason : null)
			.build();
	}

	public static ReportCreateResponse toReportCreateResponse(Report report) {
		return ReportCreateResponse.builder()
			.reportId(report.getId())
			.build();
	}
}
