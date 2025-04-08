package im.toduck.global.config.web.converter;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class YearMonthConverter implements Converter<String, YearMonth> {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

	@Override
	public YearMonth convert(String source) {
		return YearMonth.parse(source, FORMATTER);
	}
}
