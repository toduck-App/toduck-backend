package im.toduck.infra.redis.phonenumber;

import static im.toduck.fixtures.PhoneNumberFixtures.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import im.toduck.ServiceTest;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;

class PhoneNumberServiceImplTest extends ServiceTest {

	@Autowired
	private PhoneNumberService phoneNumberService;

	private PhoneNumber buildPhoneNumberEntity;
	private PhoneNumber errorPhoneNumberEntity;
	private PhoneNumber buildMaxVerifyCountPhoneNumberEntity;
	private PhoneNumber buildSuccessVerifiedPhoneNumberEntity;

	private String reVerifyCode;

	private Integer maxMessageCount;

	@BeforeEach
	void setUp() {
		buildPhoneNumberEntity = testFixtureBuilder.buildPhoneNumber(GENERAL_PHONE_NUMBER());
		errorPhoneNumberEntity = ERROR_PHONE_NUMBER();
		buildMaxVerifyCountPhoneNumberEntity = testFixtureBuilder.buildPhoneNumber(MAX_VERIFY_PHONE_NUMBER());
		buildSuccessVerifiedPhoneNumberEntity = testFixtureBuilder.buildPhoneNumber(SUCCESS_VERIFIED_PHONE_NUMBER());
		reVerifyCode = "99999";
		maxMessageCount = PhoneNumber.getMaxMessageCount();
	}

	@Nested
	@DisplayName("<전화번호 캐시 조회 테스트>")
	class findAlreadySentPhoneNumberTest {
		@Test
		void 캐시_조회_성공() {
			// given
			String findPhoneNumber = buildPhoneNumberEntity.getPhoneNumber();

			//when
			PhoneNumber alreadySentPhoneNumber = phoneNumberService.findAlreadySentPhoneNumber(findPhoneNumber).get();

			//then
			assertSoftly(softly -> {
				softly.assertThat(alreadySentPhoneNumber.getPhoneNumber())
					.isEqualTo(buildPhoneNumberEntity.getPhoneNumber());
				softly.assertThat(alreadySentPhoneNumber.getVerifyCode())
					.isEqualTo(buildPhoneNumberEntity.getVerifyCode());
				softly.assertThat(alreadySentPhoneNumber.getIsVerified())
					.isEqualTo(buildPhoneNumberEntity.getIsVerified());
				softly.assertThat(alreadySentPhoneNumber.getSendMessageCount())
					.isEqualTo(buildPhoneNumberEntity.getSendMessageCount());
				softly.assertThat(alreadySentPhoneNumber.getVerifyCount())
					.isEqualTo(buildPhoneNumberEntity.getVerifyCount());
			});
		}

		@Test
		void 캐시_조회_실패_캐시에존재하지않음() {
			// given
			String errorPhoneNumber = errorPhoneNumberEntity.getPhoneNumber();

			//when
			Optional<PhoneNumber> alreadySentPhoneNumber = phoneNumberService.findAlreadySentPhoneNumber(
				errorPhoneNumber);

			//then
			assertSoftly(softly -> {
				softly.assertThat(alreadySentPhoneNumber).isEqualTo(Optional.empty());
			});
		}
	}

	@Nested
	@DisplayName("<전화번호 인증코드 재전송 테스트>")
	class reSendVerifiedCodeToPhoneNumberTest {
		@Test
		void 인증코드_재전송_성공() {
			// given
			PhoneNumber phoneNumber = buildPhoneNumberEntity;
			Integer preSendMessageCount = phoneNumber.getSendMessageCount();
			given(verifiyCodeUtil.generateVerifyCode()).willReturn(reVerifyCode);
			//when
			phoneNumberService.reSendVerifiedCodeToPhoneNumber(phoneNumber);
			PhoneNumber changedPhoneNumberEntity = phoneNumberService.findAlreadySentPhoneNumber(
				phoneNumber.getPhoneNumber()).get();

			//then
			assertSoftly(softly -> {
				softly.assertThat(changedPhoneNumberEntity.getPhoneNumber()).isEqualTo(phoneNumber.getPhoneNumber());
				softly.assertThat(changedPhoneNumberEntity.getVerifyCode()).isEqualTo(reVerifyCode);
				softly.assertThat(changedPhoneNumberEntity.getSendMessageCount()).isEqualTo(preSendMessageCount + 1);
				softly.assertThat(changedPhoneNumberEntity.getVerifyCount()).isEqualTo(phoneNumber.getVerifyCount());
				softly.assertThat(changedPhoneNumberEntity.getIsVerified()).isEqualTo(phoneNumber.getIsVerified());
			});
		}

		@Test
		void 인증코드_재전송_실패_메세지전송횟수초과() {
			// given
			PhoneNumber phoneNumber = buildPhoneNumberEntity;
			given(verifiyCodeUtil.generateVerifyCode()).willReturn(reVerifyCode);

			for (int i = phoneNumber.getSendMessageCount(); i < maxMessageCount; i++) {
				phoneNumberService.reSendVerifiedCodeToPhoneNumber(phoneNumber);
			}

			//then
			assertSoftly(softly -> {
				softly.assertThatThrownBy(() -> phoneNumberService.reSendVerifiedCodeToPhoneNumber(phoneNumber))
					.isInstanceOf(CommonException.class)
					.hasFieldOrPropertyWithValue("errorCode", ExceptionCode.OVER_MAX_MESSAGE_COUNT.getErrorCode())
					.hasFieldOrPropertyWithValue("message", ExceptionCode.OVER_MAX_MESSAGE_COUNT.getMessage())
					.hasFieldOrPropertyWithValue("httpStatus", ExceptionCode.OVER_MAX_MESSAGE_COUNT.getHttpStatus());

			});
		}

	}

	@Nested
	@DisplayName("<전화번호 인증코드 전송 테스트>")
	class sendVerifiedCodeToPhoneNumberTest {
		@Test
		void 인증코드_전송_성공() {
			// given
			String phoneNumber = buildPhoneNumberEntity.getPhoneNumber();
			given(verifiyCodeUtil.generateVerifyCode()).willReturn(buildPhoneNumberEntity.getVerifyCode());
			//when
			phoneNumberService.sendVerifiedCodeToPhoneNumber(phoneNumber);
			PhoneNumber savendPhoneNumber = phoneNumberService.findAlreadySentPhoneNumber(phoneNumber).get();

			//then
			assertSoftly(softly -> {
				softly.assertThat(savendPhoneNumber.getPhoneNumber())
					.isEqualTo(buildPhoneNumberEntity.getPhoneNumber());
				softly.assertThat(savendPhoneNumber.getVerifyCode()).isEqualTo(buildPhoneNumberEntity.getVerifyCode());
				softly.assertThat(savendPhoneNumber.getSendMessageCount())
					.isEqualTo(buildPhoneNumberEntity.getSendMessageCount());
				softly.assertThat(savendPhoneNumber.getVerifyCount())
					.isEqualTo(buildPhoneNumberEntity.getVerifyCount());
				softly.assertThat(savendPhoneNumber.getIsVerified()).isEqualTo(buildPhoneNumberEntity.getIsVerified());
			});
		}
	}

	@Nested
	@DisplayName("<사용자가 보낸 인증코드 검증 테스트>")
	class validateVerifiedCodeTest {
		@Test
		void 인증코드_검증_성공() {
			// given
			PhoneNumber phoneNumber = buildPhoneNumberEntity;
			String verifiedCode = phoneNumber.getVerifyCode();
			Integer preVerifyCount = phoneNumber.getVerifyCount();
			//when
			phoneNumberService.validateVerifiedCode(phoneNumber, verifiedCode);
			PhoneNumber changedPhoneNumberEntity = phoneNumberService.findAlreadySentPhoneNumber(
				phoneNumber.getPhoneNumber()).get();

			//then
			assertSoftly(softly -> {
				softly.assertThat(changedPhoneNumberEntity.getPhoneNumber()).isEqualTo(phoneNumber.getPhoneNumber());
				softly.assertThat(changedPhoneNumberEntity.getVerifyCode()).isEqualTo(phoneNumber.getVerifyCode());
				softly.assertThat(changedPhoneNumberEntity.getSendMessageCount())
					.isEqualTo(phoneNumber.getSendMessageCount());
				softly.assertThat(changedPhoneNumberEntity.getVerifyCount()).isEqualTo(preVerifyCount + 1);
				softly.assertThat(changedPhoneNumberEntity.getIsVerified()).isEqualTo(true);
			});
		}

		@Test
		void 인증코드_검증_실패_인증횟수초과() {
			// given
			PhoneNumber phoneNumber = buildMaxVerifyCountPhoneNumberEntity;

			// when -> then
			assertSoftly(softly -> {
				softly.assertThatThrownBy(
						() -> phoneNumberService.validateVerifiedCode(phoneNumber, phoneNumber.getVerifyCode()))
					.isInstanceOf(CommonException.class)
					.hasFieldOrPropertyWithValue("errorCode", ExceptionCode.OVER_MAX_VERIFIED_COUNT.getErrorCode())
					.hasFieldOrPropertyWithValue("message", ExceptionCode.OVER_MAX_VERIFIED_COUNT.getMessage())
					.hasFieldOrPropertyWithValue("httpStatus", ExceptionCode.OVER_MAX_VERIFIED_COUNT.getHttpStatus());
			});
		}

		@Test
		void 인증코드_검증_실패_불일치() {
			// given
			PhoneNumber phoneNumber = buildPhoneNumberEntity;
			Integer preVerifyCount = phoneNumber.getVerifyCount();
			String verifiedCode = ERROR_VERIFY_CODE;
			// when -> then
			assertSoftly(softly -> {
				softly.assertThatThrownBy(() -> phoneNumberService.validateVerifiedCode(phoneNumber, verifiedCode))
					.isInstanceOf(CommonException.class)
					.hasFieldOrPropertyWithValue("errorCode", ExceptionCode.INVALID_VERIFIED_CODE.getErrorCode())
					.hasFieldOrPropertyWithValue("message", ExceptionCode.INVALID_VERIFIED_CODE.getMessage())
					.hasFieldOrPropertyWithValue("httpStatus", ExceptionCode.INVALID_VERIFIED_CODE.getHttpStatus());

				softly.assertThat(phoneNumber.getVerifyCount()).isEqualTo(preVerifyCount + 1);
			});
		}

	}

	@Nested
	@DisplayName("<전화번호 인증 여부 검증 테스트>")
	class validateVerifiedPhoneNumberTest {
		@Test
		void 인증된_전화번호인지_검증_성공() {
			// given
			String phoneNumber = buildSuccessVerifiedPhoneNumberEntity.getPhoneNumber();
			// when
			phoneNumberService.validateVerifiedPhoneNumber(phoneNumber);
			// then
			assertSoftly(softly -> {
				softly.assertThatCode(() -> phoneNumberService.validateVerifiedPhoneNumber(phoneNumber))
					.doesNotThrowAnyException();
			});
		}

		@Test
		void 인증된_전화번호인지_검증_실패_캐시에존재하지않음() {
			// given
			String phoneNumber = ERROR_PHONE_NUMBER;
			// when -> then
			assertSoftly(softly -> {
				softly.assertThatThrownBy(() -> phoneNumberService.validateVerifiedPhoneNumber(phoneNumber))
					.isInstanceOf(CommonException.class)
					.hasFieldOrPropertyWithValue("errorCode", ExceptionCode.NOT_SEND_PHONE_NUMBER.getErrorCode())
					.hasFieldOrPropertyWithValue("message", ExceptionCode.NOT_SEND_PHONE_NUMBER.getMessage())
					.hasFieldOrPropertyWithValue("httpStatus", ExceptionCode.NOT_SEND_PHONE_NUMBER.getHttpStatus());
			});
		}

		@Test
		void 인증된_전화번호인지_검증_실패_비인증() {
			// given
			String phoneNumber = buildPhoneNumberEntity.getPhoneNumber();
			// when -> then
			assertSoftly(softly -> {
				softly.assertThatThrownBy(() -> phoneNumberService.validateVerifiedPhoneNumber(phoneNumber))
					.isInstanceOf(CommonException.class)
					.hasFieldOrPropertyWithValue("errorCode", ExceptionCode.NOT_VERIFIED_PHONE_NUMBER.getErrorCode())
					.hasFieldOrPropertyWithValue("message", ExceptionCode.NOT_VERIFIED_PHONE_NUMBER.getMessage())
					.hasFieldOrPropertyWithValue("httpStatus", ExceptionCode.NOT_VERIFIED_PHONE_NUMBER.getHttpStatus());
			});
		}
	}

}
