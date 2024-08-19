package im.toduck.fixtures;

import im.toduck.infra.redis.phonenumber.PhoneNumber;

public class PhoneNumberFixtures {
	public static final String PHONE_NUMBER = "01012345678";
	public static final String VERIFY_CODE = "12345";
	public static final String ERROR_PHONE_NUMBER = "010XXXXYYYY";
	public static final String ERROR_VERIFY_CODE = "XXXXX";
	public static final String MAX_MESSAGE_COUNT_PHONE_NUMBER = "010MMMMNNNN";
	public static final String MAX_MESSAGE_COUNT_VERIFY_CODE = "XXXXX";
	public static final String SUCCESS_VERIFY_PHONE_NUMBER = "010TTTTUUUU";
	public static final String SUCCESS_VERIFY_VERIFY_CODE = "XXXXX";

	public static PhoneNumber GENERAL_PHONE_NUMBER() {
		return PhoneNumber.from(PHONE_NUMBER, VERIFY_CODE);
	}

	public static PhoneNumber ERROR_PHONE_NUMBER() {
		return PhoneNumber.from(ERROR_PHONE_NUMBER, ERROR_VERIFY_CODE);
	}

	public static PhoneNumber MAX_VERIFY_PHONE_NUMBER() {
		PhoneNumber phoneNumber = PhoneNumber.from(MAX_MESSAGE_COUNT_PHONE_NUMBER, MAX_MESSAGE_COUNT_VERIFY_CODE);
		for (int i = phoneNumber.getVerifyCount(); i < PhoneNumber.getMaxVerifedCount(); i++) {
			phoneNumber.countVerifyCount();
		}
		return phoneNumber;
	}

	public static PhoneNumber SUCCESS_VERIFIED_PHONE_NUMBER() {
		PhoneNumber phoneNumber = PhoneNumber.from(SUCCESS_VERIFY_PHONE_NUMBER, SUCCESS_VERIFY_VERIFY_CODE);
		phoneNumber.changeTrueVerify();
		return phoneNumber;
	}
}
