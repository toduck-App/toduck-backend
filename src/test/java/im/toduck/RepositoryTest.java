package im.toduck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import im.toduck.builder.BuilderSupporter;
import im.toduck.builder.TestFixtureBuilder;
import im.toduck.global.config.JpaAuditingConfig;
import im.toduck.infra.redis.phonenumber.PhoneNumberRepository;

@DataJpaTest
@ActiveProfiles("test")
@Import(value = {TestFixtureBuilder.class, BuilderSupporter.class, JpaAuditingConfig.class})
public abstract class RepositoryTest {

	@Autowired
	protected TestFixtureBuilder testFixtureBuilder;

	@MockBean
	protected PhoneNumberRepository phoneNumberRepository;
}
