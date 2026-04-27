package com.bank.devsu.dto.movimiento;

import java.math.BigDecimal;

public class MovimientoRequest {

    private String tipoMovimiento;
    private BigDecimal valor;
    private Long cuentaId;

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }
}
