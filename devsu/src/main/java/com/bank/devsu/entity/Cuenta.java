package com.bank.devsu.entity;

import java.math.BigDecimal;
import java.util.List;

import com.bank.devsu.enums.TipoCuenta;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "cuentas")
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cuentaId;

    @Column(nullable = false, unique = true)
    private String numeroCuenta;

    @Column(nullable = false)
    private TipoCuenta tipoCuenta;

    @Column(nullable = false)
    private BigDecimal saldoInicial;

    @Column(nullable = false)
    private BigDecimal saldoDisponible;

    @Column(nullable = false)
    private Boolean estado;

    @Column
    private BigDecimal limiteDiarioRetiro;

    @Column
    private BigDecimal totalRetirosDia = BigDecimal.ZERO;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", insertable = true, updatable = true)
    @JsonBackReference
    private Cliente cliente;

    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Movimiento> movimientos;

    public Cuenta() {}

    public Cuenta(String numeroCuenta, TipoCuenta tipoCuenta, BigDecimal saldoInicial, Boolean estado, Cliente cliente) {
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
        this.saldoInicial = saldoInicial;
        this.saldoDisponible = saldoInicial;
        this.estado = estado;
        this.cliente = cliente;

    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Long cuentaId) {
        this.cuentaId = cuentaId;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getSaldoDisponible() {
        return saldoDisponible;
    }

    public void setSaldoDisponible(BigDecimal saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public BigDecimal getLimiteDiarioRetiro() {
        return limiteDiarioRetiro;
    }

    public void setLimiteDiarioRetiro(BigDecimal limiteDiarioRetiro) {
        this.limiteDiarioRetiro = limiteDiarioRetiro;
    }

    public BigDecimal getTotalRetirosDia() {
        return totalRetirosDia;
    }

    public void setTotalRetirosDia(BigDecimal totalRetirosDia) {
        this.totalRetirosDia = totalRetirosDia;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Movimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }

}
