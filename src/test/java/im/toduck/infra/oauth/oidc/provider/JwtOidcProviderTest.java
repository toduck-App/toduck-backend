package im.toduck.infra.oauth.oidc.provider;

import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.ObjectMapper;

import im.toduck.global.exception.CommonException;
import io.jsonwebtoken.Claims;

class JwtOidcProviderTest {

	// 테스트에 필요한 상수 선언
	private static final String TEST_TOKEN = "header.payload.signature";
	private static final String TEST_KID = "testKid";
	private static final String TEST_ISS = "testIssuer";
	private static final String TEST_SUB = "testSubject";
	private static final String TEST_AUD = "testAudience";
	private static final String TEST_NONCE = "testNonce";
	private static final String TEST_EMAIL = "test@example.com";
	private static final String TEST_MODULUS = "testModulus";
	private static final String TEST_EXPONENT = "testExponent";

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private Claims claims;

	@InjectMocks
	private JwtOidcProvider jwtOidcProvider;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		// Claims 객체 설정 (Mock 데이터로 설정)
		given(claims.getIssuer()).willReturn(TEST_ISS);
		given(claims.getAudience()).willReturn(Set.of(TEST_AUD));
		given(claims.getSubject()).willReturn(TEST_SUB);
		given(claims.get("email", String.class)).willReturn(TEST_EMAIL);
	}

	// getKidFromUnsignedTokenHeader 성공 케이스 테스트
	@Test
	void getKidFromUnsignedTokenHeader_정상적인_KID_추출() throws Exception {
		// given
		Map<String, String> header = Map.of("kid", TEST_KID);
		Map<String, String> payload = Map.of(
			"iss", TEST_ISS,
			"sub", TEST_SUB,
			"aud", TEST_AUD,
			"nonce", TEST_NONCE
		);
		when(objectMapper.readValue(anyString(), eq(Map.class))).thenReturn(header).thenReturn(payload);

		// when
		String result = jwtOidcProvider.getKidFromUnsignedTokenHeader(TEST_TOKEN, TEST_ISS, TEST_SUB, TEST_AUD,
			TEST_NONCE);

		// then
		assertSoftly(softly -> {
			softly.assertThat(result).isEqualTo(TEST_KID);
		});
		verify(objectMapper, times(2)).readValue(anyString(), eq(Map.class));
	}

	@Test
	void getKidFromUnsignedTokenHeader_잘못된_토큰_예외발생() throws Exception {
		// given
		String invalidToken = "invalid.token";

		// when & then
		assertSoftly(softly -> {
			softly.assertThatThrownBy(() -> {
				jwtOidcProvider.getKidFromUnsignedTokenHeader(invalidToken, TEST_ISS, TEST_SUB, TEST_AUD, TEST_NONCE);
			}).isInstanceOf(CommonException.class);
		});
	}

	@Test
	void getOidcTokenBody_성공적인_토큰_추출() {
		//TODO: 테스트 코드 작성해야함
	}

	@Test
	void getOidcTokenBody_잘못된_토큰_예외발생() {
		// given
		String invalidToken = "invalid.token";

		// when & then
		assertSoftly(softly -> {
			softly.assertThatThrownBy(() -> {
				jwtOidcProvider.getOidcTokenBody(invalidToken, TEST_MODULUS, TEST_EXPONENT);
			}).isInstanceOf(CommonException.class);
		});
	}
}
