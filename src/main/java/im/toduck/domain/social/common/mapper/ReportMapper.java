package im.toduck.domain.social.common.mapper;

import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.CommentReport;
import im.toduck.domain.social.persistence.entity.ReportType;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialReport;
import im.toduck.domain.social.presentation.dto.response.ReportCreateResponse;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportMapper {
	public static SocialReport toReport(
		final User user,
		final Social social,
		final ReportType reportType,
		final String reason
	) {
		return SocialReport.builder()
			.user(user)
			.social(social)
			.reportType(reportType)
			.reason(reportType == ReportType.OTHER ? reason : null)
			.build();
	}

	public static ReportCreateResponse toReportCreateResponse(final SocialReport report) {
		return ReportCreateResponse.builder()
			.reportId(report.getId())
			.build();
	}

	public static CommentReport toCommentReport(final User user,
		final Comment comment,
		final ReportType reportType,
		final String reason
	) {
		return CommentReport.builder()
			.user(user)
			.comment(comment)
			.reportType(reportType)
			.reason(reportType == ReportType.OTHER ? reason : null)
			.build();
	}

	public static ReportCreateResponse toReportCreateResponse(final CommentReport report) {
		return ReportCreateResponse.builder()
			.reportId(report.getId())
			.build();
	}
}
