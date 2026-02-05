package im.toduck.domain.admin.domain.usecase;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.admin.common.mapper.AdminMapper;
import im.toduck.domain.admin.domain.service.AdminService;
import im.toduck.domain.admin.persistence.entity.Admin;
import im.toduck.domain.admin.presentation.dto.request.AdminCreateRequest;
import im.toduck.domain.admin.presentation.dto.request.AdminUpdateRequest;
import im.toduck.domain.admin.presentation.dto.response.AdminListResponse;
import im.toduck.domain.admin.presentation.dto.response.AdminResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.entity.UserRole;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class AdminUseCase {
	private final AdminService adminService;
	private final UserService userService;

	@Transactional
	public AdminResponse getAdmin(final Long userId) {
		Admin admin = adminService.getAdmin(userId);

		return AdminMapper.toAdminResponse(admin);
	}

	@Transactional
	public AdminListResponse getAdmins() {
		List<AdminResponse> admins = adminService.getAdmins();

		return AdminMapper.toLAdminListResponse(admins);
	}

	@Transactional
	public Admin createAdmin(final AdminCreateRequest request) {
		User user = userService.getUserById(request.userId())
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		Admin existingAdmin = adminService.getExistingAdmin(user.getId()).orElse(null);

		// 이미 활성화된 관리자인 경우
		if (existingAdmin != null && existingAdmin.getDeletedAt() == null) {
			throw CommonException.from(ExceptionCode.DUPLICATE_ADMIN);
		}

		// 삭제된 관리자 복구
		if (existingAdmin != null) {
			existingAdmin.revive();
			existingAdmin.updateDisplayName(request.displayName());

			if (user.getRole() == UserRole.USER) {
				user.promoteToAdmin();
			}

			return existingAdmin;
		}

		// 신규 관리자
		Admin admin = adminService.createAdmin(request, user);

		if (user.getRole() == UserRole.USER) {
			user.promoteToAdmin();
		}

		return admin;
	}

	@Transactional
	public void updateAdmin(final Long userId, final AdminUpdateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		Admin admin = adminService.getAdmin(userId);

		adminService.updateAdmin(userId, request);
	}

	@Transactional
	public void deleteAdmin(final Long userId) {
		Admin admin = adminService.getAdmin(userId);

		if (admin.getDeletedAt() != null) {
			return;
		}

		User user = admin.getUser();
		user.demoteToUser();

		adminService.deleteAdmin(admin);
	}
}
