package im.toduck.domain.schedule.common.converter;

import im.toduck.global.helper.DaysOfWeekBitmask;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ScheduleDaysOfWeekBitmaskConverter implements AttributeConverter<DaysOfWeekBitmask, Byte> {
	@Override
	public Byte convertToDatabaseColumn(DaysOfWeekBitmask attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.getValue();
	}

	@Override
	public DaysOfWeekBitmask convertToEntityAttribute(Byte dbData) {
		if (dbData == null) {
			return null;
		}
		return DaysOfWeekBitmask.from(dbData);
	}
}
