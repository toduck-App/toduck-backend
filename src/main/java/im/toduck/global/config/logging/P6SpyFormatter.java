package im.toduck.global.config.logging;

import java.util.Locale;
import java.util.Set;

import org.hibernate.engine.jdbc.internal.FormatStyle;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

public class P6SpyFormatter implements MessageFormattingStrategy {

	private static final String NEW_LINE = System.lineSeparator();
	private static final String TAB = "\t";
	private static final Set<String> DDL_KEYWORDS = Set.of("create", "alter", "drop", "comment");
	private static final String CONNECTION_ID_FORMAT = "Connection ID: %s";
	private static final String SEPARATOR = "-".repeat(200);

	@Override
	public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared,
		String sql, String url) {
		if (sql.trim().isEmpty()) {
			return formatByCommand(category, connectionId);
		}
		return formatBySql(sql, category) + formatAdditionalInfo(elapsed, connectionId);
	}

	private static String formatByCommand(String category, int connectionId) {
		return String.format("%s | Category: %s", String.format(CONNECTION_ID_FORMAT, connectionId), category);
	}

	private String formatBySql(String sql, String category) {
		String formattedSql = isStatementDdl(sql, category)
			? formatDdl(sql)
			: formatDml(sql);

		return NEW_LINE + removeTimeZoneOffset(formattedSql);
	}

	private String formatDdl(String sql) {
		return NEW_LINE + "Execute DDL : " + FormatStyle.DDL.getFormatter().format(sql);
	}

	private String formatDml(String sql) {
		return NEW_LINE + "Execute DML : " + FormatStyle.BASIC.getFormatter().format(sql);
	}

	private String formatAdditionalInfo(long elapsed, int connectionId) {
		return String.join(
			NEW_LINE,
			"",
			"",
			TAB + String.format(CONNECTION_ID_FORMAT, connectionId),
			TAB + String.format("Execution Time: %s ms", elapsed),
			"",
			SEPARATOR
		);
	}

	private boolean isStatementDdl(String sql, String category) {
		return Category.STATEMENT.getName().equals(category) && isDdl(sql.trim().toLowerCase(Locale.ROOT));
	}

	private boolean isDdl(String lowerSql) {
		return DDL_KEYWORDS.stream().anyMatch(lowerSql::startsWith);
	}

	private String removeTimeZoneOffset(String sql) {
		return sql.replace("+0900", "");
	}
}
