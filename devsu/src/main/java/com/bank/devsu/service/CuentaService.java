package com.bank.devsu.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.devsu.config.CuentaConfigProperties;
import com.bank.devsu.entity.Cliente;
import com.bank.devsu.entity.Cuenta;
import com.bank.devsu.enums.TipoCuenta;
import com.bank.devsu.repository.ClienteRepository;
import com.bank.devsu.repository.CuentaRepository;

@Service
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CuentaConfigProperties cuentaConfig;

    public List<Cuenta> obtenerTodas() {
        return cuentaRepository.findAll();
    }

    public Optional<Cuenta> obtenerPorId(Long cuentaId) {
        return cuentaRepository.findById(cuentaId);
    }

    public Optional<Cuenta> obtenerPorNumeroCuenta(String numeroCuenta) {
        return cuentaRepository.findByNumeroCuenta(numeroCuenta);
    }

    public List<Cuenta> obtenerPorClienteId(Long personaId) {
        return cuentaRepository.findByClientePersonaId(personaId);
    }

    public List<Cuenta> buscarPorNumeroCuenta(String numeroCuenta) {
        return cuentaRepository.findByNumeroCuentaContainingIgnoreCase(numeroCuenta);
    }

    public Cuenta crear(Cuenta cuenta, Long personaId) {
        if (cuentaRepository.findByNumeroCuenta(cuenta.getNumeroCuenta()).isPresent()) {
            throw new IllegalArgumentException("El número de cuenta ya existe");
        }
        System.out.println("personaId "+personaId);

        Optional<Cliente> cliente = clienteRepository.findById(personaId);
        if (cliente.isEmpty()) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }


        cuenta.setCliente(cliente.get());
        cuenta.setSaldoDisponible(cuenta.getSaldoInicial());
        cuenta.setLimiteDiarioRetiro(cuentaConfig.getLimiteDiarioRetiro());

        return cuentaRepository.save(cuenta);
    }

    public Cuenta actualizar(Long cuentaId, Cuenta cuentaActualizada) {
        Optional<Cuenta> cuenta = cuentaRepository.findById(cuentaId);
        if (cuenta.isEmpty()) {
            throw new IllegalArgumentException("Cuenta no encontrada");
        }

        Cuenta c = cuenta.get();
        c.setNumeroCuenta(cuentaActualizada.getNumeroCuenta());
        c.setTipoCuenta(cuentaActualizada.getTipoCuenta());
        c.setEstado(cuentaActualizada.getEstado());

        return cuentaRepository.save(c);
    }

    public void eliminar(Long cuentaId) {
        if (!cuentaRepository.existsById(cuentaId)) {
            throw new IllegalArgumentException("Cuenta no encontrada");
        }
        cuentaRepository.deleteById(cuentaId);
    }

    public void validarSaldoDisponible(Cuenta cuenta, BigDecimal monto) {
        if (cuenta.getSaldoDisponible().compareTo(monto) < 0) {
            throw new IllegalArgumentException("Saldo no disponible");
        }
    }

    public void validarLimiteDiarioRetiro(Cuenta cuenta, BigDecimal monto) {
        if (cuenta.getTipoCuenta() == TipoCuenta.AHORROS) {

            if (cuenta.getTotalRetirosDia().add(monto).compareTo(cuenta.getLimiteDiarioRetiro()) > 0) {
                throw new IllegalArgumentException("Cupo diario Excedido");
            }
        }
    }

    public void actualizarSaldoYRetiros(Cuenta cuenta, BigDecimal monto, String tipoMovimiento) {
        if ("Débito".equalsIgnoreCase(tipoMovimiento) || "Retiro".equalsIgnoreCase(tipoMovimiento)) {
            cuenta.setSaldoDisponible(cuenta.getSaldoDisponible().subtract(monto));
            if (cuenta.getTipoCuenta() == TipoCuenta.AHORROS) {
                cuenta.setTotalRetirosDia(cuenta.getTotalRetirosDia().add(monto));
            }
        } else if ("Crédito".equalsIgnoreCase(tipoMovimiento) || "Depósito".equalsIgnoreCase(tipoMovimiento)) {
            cuenta.setSaldoDisponible(cuenta.getSaldoDisponible().add(monto));
        }
        cuentaRepository.save(cuenta);
    }

    public void resetearRetirosDiarios() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        for (Cuenta cuenta : cuentas) {
            cuenta.setTotalRetirosDia(BigDecimal.ZERO);
            cuentaRepository.save(cuenta);
        }
    }
}
