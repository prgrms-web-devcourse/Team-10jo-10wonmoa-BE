package com.prgrms.tenwonmoa.domain.budget.converter;

import java.time.YearMonth;

import javax.persistence.AttributeConverter;

public class YearMonthIntegerAttributeConverter implements AttributeConverter<YearMonth, Integer> {
	@Override
	public Integer convertToDatabaseColumn(YearMonth attribute) {
		if (attribute != null) {
			return (attribute.getYear() * 100) + attribute.getMonth().getValue();
		}
		return null;
	}

	@Override
	public YearMonth convertToEntityAttribute(Integer dbData) {
		if (dbData != null) {
			int year = dbData / 100;
			int month = dbData % 100;
			return YearMonth.of(year, month);
		}
		return null;
	}
}
