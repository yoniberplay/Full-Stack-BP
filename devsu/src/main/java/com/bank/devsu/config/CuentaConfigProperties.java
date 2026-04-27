package com.bank.devsu.config;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cuenta")
public class CuentaConfigProperties {

    private BigDecimal limiteDiarioRetiro = new BigDecimal("1000");

    public BigDecimal getLimiteDiarioRetiro() {
        return limiteDiarioRetiro;
    }

    public void setLimiteDiarioRetiro(BigDecimal limiteDiarioRetiro) {
        this.limiteDiarioRetiro = limiteDiarioRetiro;
    }
}
