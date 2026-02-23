package im.toduck.infra.redis.weblogin;

import org.springframework.data.repository.CrudRepository;

public interface WebLoginSessionRepository extends CrudRepository<WebLoginSession, String> {
}
