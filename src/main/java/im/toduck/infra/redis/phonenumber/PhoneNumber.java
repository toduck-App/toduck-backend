package im.toduck.infra.redis.phonenumber;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

@Getter
@RedisHash(value = "phoneNumber", timeToLive = 60 * 60 * 24)
public class PhoneNumber {
	@Id
	private String phoneNumber;
	private String verifyCode;
	private Boolean isVerified;
	private Integer sendMessageCount;
	private Integer verifyCount;

	private static final int MAX_MESSAGE_COUNT = 5;
	private static final int MAX_VERIFIED_COUNT = 5;

	private PhoneNumber(String phoneNumber, String verifyCode) {
		this.phoneNumber = phoneNumber;
		this.verifyCode =  verifyCode;
		this.isVerified = false;
		this.sendMessageCount = 1;
		this.verifyCount = 1;
	}

	public static PhoneNumber from(String phoneNumber, String verifyCode) {
		return new PhoneNumber(phoneNumber, verifyCode);
	}

	public boolean isMaxMessageCount() {
		return this.sendMessageCount >= MAX_MESSAGE_COUNT;
	}

	public void countMessageCount() {
		this.sendMessageCount++;
	}

	public void reSetVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public boolean isVerifiedCode(String verifiedCode) {
		return this.verifyCode.equals(verifiedCode);
	}

	public boolean isMaxVerifyCount() {
		return this.verifyCount >= MAX_VERIFIED_COUNT;
	}

	public void countVerifyCount() {
		this.verifyCount++;
	}

	public void changeTrueVerify() {
		this.isVerified = true;
	}

	public boolean isVerified() {
		return this.isVerified;
	}
}
