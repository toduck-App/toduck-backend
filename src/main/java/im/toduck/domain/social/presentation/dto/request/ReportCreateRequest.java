package im.toduck.domain.social.presentation.dto.request;

public record ReportCreateRequest(
	String reason,
	boolean isBlockAuthor
) {
}
