package com.bank.devsu.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReporteDTO {

    private String clienteNombre;
    private String identificacion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private List<CuentaReporte> cuentas;
    private BigDecimal totalDebitos;
    private BigDecimal totalCreditos;

    public static class CuentaReporte {
        private String numeroCuenta;
        private String tipoCuenta;
        private BigDecimal saldoActual;
        private BigDecimal totalDebitos;
        private BigDecimal totalCreditos;
        private List<MovimientoReporte> movimientos;

        public String getNumeroCuenta() {
            return numeroCuenta;
        }

        public void setNumeroCuenta(String numeroCuenta) {
            this.numeroCuenta = numeroCuenta;
        }

        public String getTipoCuenta() {
            return tipoCuenta;
        }

        public void setTipoCuenta(String tipoCuenta) {
            this.tipoCuenta = tipoCuenta;
        }

        public BigDecimal getSaldoActual() {
            return saldoActual;
        }

        public void setSaldoActual(BigDecimal saldoActual) {
            this.saldoActual = saldoActual;
        }

        public BigDecimal getTotalDebitos() {
            return totalDebitos;
        }

        public void setTotalDebitos(BigDecimal totalDebitos) {
            this.totalDebitos = totalDebitos;
        }

        public BigDecimal getTotalCreditos() {
            return totalCreditos;
        }

        public void setTotalCreditos(BigDecimal totalCreditos) {
            this.totalCreditos = totalCreditos;
        }

        public List<MovimientoReporte> getMovimientos() {
            return movimientos;
        }

        public void setMovimientos(List<MovimientoReporte> movimientos) {
            this.movimientos = movimientos;
        }
    }

    public static class MovimientoReporte {
        private LocalDateTime fecha;
        private String tipo;
        private BigDecimal valor;
        private BigDecimal saldo;

        public LocalDateTime getFecha() {
            return fecha;
        }

        public void setFecha(LocalDateTime fecha) {
            this.fecha = fecha;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }

        public BigDecimal getSaldo() {
            return saldo;
        }

        public void setSaldo(BigDecimal saldo) {
            this.saldo = saldo;
        }
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public List<CuentaReporte> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<CuentaReporte> cuentas) {
        this.cuentas = cuentas;
    }

    public BigDecimal getTotalDebitos() {
        return totalDebitos;
    }

    public void setTotalDebitos(BigDecimal totalDebitos) {
        this.totalDebitos = totalDebitos;
    }

    public BigDecimal getTotalCreditos() {
        return totalCreditos;
    }

    public void setTotalCreditos(BigDecimal totalCreditos) {
        this.totalCreditos = totalCreditos;
    }
}
