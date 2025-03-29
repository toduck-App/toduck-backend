package im.toduck.domain.user.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.user.persistence.entity.OAuthProvider;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.repository.querydsl.UserRepositoryCustom;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
	Optional<User> findByPhoneNumber(String username);

	Optional<User> findByLoginId(String loginId);

	boolean existsByNickname(String nickname);

	Optional<User> findByProviderAndEmail(OAuthProvider provider, String email);
}
