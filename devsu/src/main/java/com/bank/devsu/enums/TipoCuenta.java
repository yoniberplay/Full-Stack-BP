package com.bank.devsu.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoCuenta {
    AHORROS, CORRIENTE;

    @JsonCreator
    public static TipoCuenta fromString(String value) {
        if (value == null) {
			return null;
		}
        String v = value.trim().toUpperCase();
        if (v.startsWith("AHORRO")) {
			return AHORROS;
		}
        if (v.startsWith("CORRIENTE")) {
			return CORRIENTE;
		}
        throw new IllegalArgumentException("Tipo de cuenta inválido: " + value);
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}
