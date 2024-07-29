package im.toduck.global;

import lombok.Getter;

@Getter
public class VoException extends RuntimeException {
	private final String message;

	public VoException(String message) {
		this.message = message;
	}
}

