package im.toduck.domain.backoffice.common.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationPlaceholderResolver {

	public static String resolve(final String template, final String nickname) {
		if (template == null || nickname == null) {
			return template;
		}

		return template.replace("{@Username}", nickname);
	}
}
