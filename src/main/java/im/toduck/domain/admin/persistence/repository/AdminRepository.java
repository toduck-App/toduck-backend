package im.toduck.domain.admin.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.admin.persistence.entity.Admin;
import im.toduck.domain.admin.persistence.repository.querydsl.AdminRepositoryCustom;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long>, AdminRepositoryCustom {
}
