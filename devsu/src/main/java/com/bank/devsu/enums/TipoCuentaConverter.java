package com.bank.devsu.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoCuentaConverter implements AttributeConverter<TipoCuenta, String> {

    @Override
    public String convertToDatabaseColumn(TipoCuenta attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public TipoCuenta convertToEntityAttribute(String dbData) {
        return dbData == null ? null : TipoCuenta.fromString(dbData);
    }
}
