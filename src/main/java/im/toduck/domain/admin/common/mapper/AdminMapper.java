package im.toduck.domain.admin.common.mapper;

import java.util.List;

import im.toduck.domain.admin.persistence.entity.Admin;
import im.toduck.domain.admin.presentation.dto.response.AdminListResponse;
import im.toduck.domain.admin.presentation.dto.response.AdminResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminMapper {
	public static AdminResponse toAdminResponse(final Admin admin) {
		return new AdminResponse(
			admin.getId(),
			admin.getUser().getId(),
			admin.getDisplayName()
		);
	}

	public static AdminListResponse toLAdminListResponse(final List<AdminResponse> admins) {
		return AdminListResponse.toListAdminResponse(admins);
	}

	public static AdminResponse fromAdmin(final Admin admin) {
		return new AdminResponse(
			admin.getId(),
			admin.getUser().getId(),
			admin.getDisplayName()
		);
	}
}
