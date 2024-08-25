package im.toduck.domain.routine.converter;

import im.toduck.global.helper.DaysOfWeekBitmask;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DaysOfWeekBitmaskConverter implements AttributeConverter<DaysOfWeekBitmask, Byte> {
	@Override
	public Byte convertToDatabaseColumn(DaysOfWeekBitmask attribute) {
		return attribute.getValue();
	}

	@Override
	public DaysOfWeekBitmask convertToEntityAttribute(Byte dbData) {
		return DaysOfWeekBitmask.from(dbData);
	}
}
