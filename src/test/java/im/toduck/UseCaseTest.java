package im.toduck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import im.toduck.builder.BuilderSupporter;
import im.toduck.builder.TestFixtureBuilder;
import im.toduck.domain.auth.common.helper.OAuthOidcHelper;
import im.toduck.domain.auth.domain.service.JwtService;
import im.toduck.domain.auth.domain.service.NickNameGenerateService;
import im.toduck.domain.user.domain.service.UserService;

@SpringBootTest
@ActiveProfiles("test")
@Import(value = {TestFixtureBuilder.class, BuilderSupporter.class})
public abstract class UseCaseTest {
	@Autowired
	protected TestFixtureBuilder testFixtureBuilder;

	@MockBean
	protected JwtService jwtService;

	@MockBean
	protected OAuthOidcHelper oauthOidcHelper;

	@MockBean
	protected UserService userService;

	@MockBean
	protected NickNameGenerateService nickNameGenerateService;
}
