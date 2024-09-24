package im.toduck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import im.toduck.builder.BuilderSupporter;
import im.toduck.builder.TestFixtureBuilder;
import im.toduck.global.security.jwt.access.AccessTokenProvider;
import im.toduck.global.security.jwt.refresh.RefreshTokenProvider;
import im.toduck.infra.redis.forbidden.ForbiddenTokenService;
import im.toduck.infra.redis.refresh.RefreshTokenService;
import im.toduck.infra.sms.VerifiyCodeUtil;

@SpringBootTest
@ActiveProfiles("test")
@Import(value = {TestFixtureBuilder.class, BuilderSupporter.class})
public abstract class ServiceTest {
	@Autowired
	protected TestFixtureBuilder testFixtureBuilder;

	@MockBean
	protected AccessTokenProvider accessTokenProvider;

	@MockBean
	protected RefreshTokenProvider refreshTokenProvider;

	@MockBean
	protected RefreshTokenService refreshTokenService;

	@MockBean
	protected ForbiddenTokenService forbiddenTokenService;

	@MockBean
	protected VerifiyCodeUtil verifiyCodeUtil;
}
