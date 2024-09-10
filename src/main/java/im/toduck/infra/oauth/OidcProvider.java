package im.toduck.infra.oauth;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OidcProvider {
	KAKAO,
	GOOGLE,
	APPLE;

	@JsonCreator
	public OidcProvider fromString(String type) { //TODO : validator 적용 해야함
		return valueOf(type.toUpperCase());
	}

}
