package im.toduck.domain.admin.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.admin.common.mapper.AdminMapper;
import im.toduck.domain.admin.persistence.entity.Admin;
import im.toduck.domain.admin.persistence.repository.AdminRepository;
import im.toduck.domain.admin.presentation.dto.request.AdminCreateRequest;
import im.toduck.domain.admin.presentation.dto.request.AdminUpdateRequest;
import im.toduck.domain.admin.presentation.dto.response.AdminResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.entity.UserRole;
import im.toduck.domain.user.persistence.repository.UserRepository;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final UserRepository userRepository;
	private final AdminRepository adminRepository;

	@Transactional
	public Admin getAdmin(final Long userId) {
		return adminRepository.findActiveAdminByUserId(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_ADMIN));
	}

	// Role은 Admin이지만 admin 테이블에 등록되어있지 않은 경우 사용합니다.
	@Transactional
	public Admin getAdminBySameUser(final Long userId) {
		return adminRepository.findActiveAdminByUserId(userId)
			.orElseGet(() -> createDefaultAdmin(userId));
	}

	@Transactional
	private Admin createDefaultAdmin(final Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		if (user.getRole() != UserRole.ADMIN) {
			throw CommonException.from(ExceptionCode.NOT_FOUND_ADMIN);
		}

		Admin admin = Admin.builder()
			.user(user)
			.displayName("토덕 관리자")
			.build();

		return adminRepository.save(admin);
	}

	@Transactional(readOnly = true)
	public List<AdminResponse> getAdmins() {
		List<Admin> admins = adminRepository.findAllActiveAdmins();
		return admins.stream()
			.map(AdminMapper::toAdminResponse)
			.toList();
	}

	@Transactional(readOnly = true)
	public Optional<Admin> getExistingAdmin(final Long userId) {
		return adminRepository.findByUserIdIncludeDeleted(userId);
	}

	@Transactional
	public Admin createAdmin(final AdminCreateRequest request, final User user) {
		Admin admin = Admin.builder()
			.user(user)
			.displayName(request.displayName())
			.build();

		return adminRepository.save(admin);
	}

	@Transactional
	public void updateAdmin(final Long userId, final AdminUpdateRequest request) {
		if (request.displayName() != null) {
			Admin admin = adminRepository.findActiveAdminByUserId(userId)
				.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_ADMIN));
			admin.updateDisplayName(request.displayName());
		}
	}

	@Transactional
	public void deleteAdmin(final Admin admin) {
		adminRepository.delete(admin);
	}
}
