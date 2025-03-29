package im.toduck.infra.redis.phonenumber;

import java.util.Optional;

public interface PhoneNumberService {
	Optional<PhoneNumber> findAlreadySentPhoneNumber(String phoneNumber);

	void reSendVerifiedCodeToPhoneNumber(PhoneNumber phoneNumber);

	void sendVerifiedCodeToPhoneNumber(String phoneNumber);

	void validateVerifiedCode(PhoneNumber phoneNumberEntity, String verifiedCode);

	void validateVerifiedPhoneNumber(String phoneNumber);

	void deleteVerifiedPhoneNumber(String phoneNumber);
}
