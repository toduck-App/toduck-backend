package im.toduck.infra.oauth.oidc.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OidcPublicKeyResponse {
	List<OidcPublicKey> keys;
}
