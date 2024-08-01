package im.toduck.global.security.jwt.access;

import lombok.Getter;

@Getter
public enum AccessTokenClaimKeys {
	USER_ID("userId"),
	ROLE("role");

	private final String value;

	AccessTokenClaimKeys(String value) {
		this.value = value;
	}
}

