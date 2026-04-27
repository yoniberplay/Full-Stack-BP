package com.bank.devsu.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.devsu.entity.Cuenta;
import com.bank.devsu.entity.Movimiento;
import com.bank.devsu.enums.TipoCuenta;
import com.bank.devsu.repository.CuentaRepository;
import com.bank.devsu.repository.MovimientoRepository;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private CuentaService cuentaService;

    public List<Movimiento> obtenerTodos() {
        return movimientoRepository.findAll();
    }

    public Optional<Movimiento> obtenerPorId(Long movimientoId) {
        return movimientoRepository.findById(movimientoId);
    }

    public List<Movimiento> obtenerPorCuentaId(Long cuentaId) {
        return movimientoRepository.findByCuentaIdOrderByFechaDesc(cuentaId);
    }

    public List<Movimiento> obtenerPorCuentaYFechas(Long cuentaId, LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepository.findByCuentaIdAndFechaBetweenOrderByFechaDesc(cuentaId, inicio, fin);
    }

    public Movimiento crear(Movimiento movimiento) {
        Optional<Cuenta> cuenta = cuentaRepository.findById(movimiento.getCuenta().getCuentaId());
        if (cuenta.isEmpty()) {
            throw new IllegalArgumentException("Cuenta no encontrada");
        }


        if (movimiento.getValor() == null || movimiento.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El valor no permitido.");
        }


        Cuenta c = cuenta.get();

        String tipo = movimiento.getTipoMovimiento().toLowerCase();
        boolean esDebito = tipo.contains("débito") || tipo.contains("retiro");

        if (esDebito) {

        	LocalDateTime inicio = LocalDate.now().atStartOfDay();
            LocalDateTime fin = LocalDate.now().atTime(LocalTime.MAX);

            BigDecimal sumaHoy = movimientoRepository.sumRetirosDelDia(movimiento.getCuenta().getCuentaId(), inicio, fin);
            if (sumaHoy == null) {
				sumaHoy = BigDecimal.ZERO;
			}
        	BigDecimal totalProyectado = sumaHoy.add(movimiento.getValor().abs());

            cuentaService.validarSaldoDisponible(c, movimiento.getValor());
            
            BigDecimal limite = c.getLimiteDiarioRetiro();
            if (limite == null) {
                limite = new BigDecimal("1000"); 
            }

        	if (totalProyectado.compareTo(limite) > 0) {
        	    throw new IllegalArgumentException("Cupo diario Excedido");
        	}

            movimiento.setValor(movimiento.getValor().negate());
        }

        BigDecimal nuevoSaldo = c.getSaldoDisponible().add(movimiento.getValor());
        movimiento.setSaldo(nuevoSaldo);
        c.setSaldoDisponible(nuevoSaldo);

        if (esDebito && c.getTipoCuenta() == TipoCuenta.AHORROS) {
            c.setTotalRetirosDia(c.getTotalRetirosDia().add(movimiento.getValor().abs()));
        }

        movimiento.setFecha(LocalDateTime.now());
        cuentaRepository.save(c);
        return movimientoRepository.save(movimiento);
    }

    public void eliminar(Long movimientoId) {
        if (!movimientoRepository.existsById(movimientoId)) {
            throw new IllegalArgumentException("Movimiento no encontrado");
        }
        movimientoRepository.deleteById(movimientoId);
    }
}
