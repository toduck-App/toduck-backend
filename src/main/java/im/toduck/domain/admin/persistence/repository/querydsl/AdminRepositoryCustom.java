package im.toduck.domain.admin.persistence.repository.querydsl;

import java.util.List;
import java.util.Optional;

import im.toduck.domain.admin.persistence.entity.Admin;

public interface AdminRepositoryCustom {
	Optional<Admin> findActiveAdminByUserId(Long userId);

	List<Admin> findAllActiveAdmins();

	Optional<Admin> findByUserIdIncludeDeleted(Long userId);
}
