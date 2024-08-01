package im.toduck.global.security.jwt.refresh;

import lombok.Getter;

@Getter
public enum RefreshTokenClaimKeys {
	USER_ID("userId"),
	ROLE("role");

	private final String value;

	RefreshTokenClaimKeys(String value) {
		this.value = value;
	}
}

