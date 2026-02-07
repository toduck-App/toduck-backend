package im.toduck.domain.admin.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "관리자 목록 응답")
public record AdminListResponse(
	@Schema(description = "관리자 목록")
	List<AdminResponse> adminDtos
) {
	public static AdminListResponse toListAdminResponse(
		final List<AdminResponse> admins
	) {
		return AdminListResponse.builder()
			.adminDtos(admins)
			.build();
	}
}
