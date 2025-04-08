package im.toduck.infra.s3.presentation.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public enum ImageExtension {
	JPG("jpg", "image/jpeg"),
	JPEG("jpeg", "image/jpeg"),
	PNG("png", "image/png"),
	GIF("gif", "image/gif"),
	BMP("bmp", "image/bmp"),
	WEBP("webp", "image/webp");

	private final String extension;
	private final String mimeType;

	private static final Map<String, ImageExtension> EXTENSION_MAP = new HashMap<>();

	static {
		for (ImageExtension ie : ImageExtension.values()) {
			EXTENSION_MAP.put(ie.extension, ie);
		}
	}

	ImageExtension(final String extension, final String mimeType) {
		this.extension = extension;
		this.mimeType = mimeType;
	}

	public static boolean isSupportedExtension(final String ext) {
		return EXTENSION_MAP.containsKey(ext.toLowerCase());
	}

	public static String findMimeType(final String ext) {
		ImageExtension imageExt = EXTENSION_MAP.get(ext.toLowerCase());
		return (imageExt == null) ? null : imageExt.getMimeType();
	}
}
