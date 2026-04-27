package com.bank.devsu.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.devsu.dto.ReporteDTO;
import com.bank.devsu.entity.Cliente;
import com.bank.devsu.entity.Cuenta;
import com.bank.devsu.entity.Movimiento;
import com.bank.devsu.repository.ClienteRepository;
import com.bank.devsu.repository.CuentaRepository;
import com.bank.devsu.repository.MovimientoRepository;

@Service
public class ReporteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    public ReporteDTO generarReporte(Long personaId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Optional<Cliente> cliente = clienteRepository.findById(personaId);
        if (cliente.isEmpty()) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }

        Cliente c = cliente.get();
        List<Cuenta> cuentas = cuentaRepository.findByClientePersonaId(personaId);

        ReporteDTO reporte = new ReporteDTO();
        reporte.setClienteNombre(c.getNombre());
        reporte.setIdentificacion(c.getIdentificacion());
        reporte.setFechaInicio(fechaInicio);
        reporte.setFechaFin(fechaFin);

        List<ReporteDTO.CuentaReporte> cuentasReporte = new ArrayList<>();
        BigDecimal totalDebitos = BigDecimal.ZERO;
        BigDecimal totalCreditos = BigDecimal.ZERO;

        for (Cuenta cuenta : cuentas) {
            ReporteDTO.CuentaReporte cuentaReporte = new ReporteDTO.CuentaReporte();
            cuentaReporte.setNumeroCuenta(cuenta.getNumeroCuenta());
            cuentaReporte.setTipoCuenta(cuenta.getTipoCuenta().name());
            cuentaReporte.setSaldoActual(cuenta.getSaldoDisponible());

            List<Movimiento> movimientos = movimientoRepository.findByCuentaIdAndFechaBetweenOrderByFechaDesc(
                    cuenta.getCuentaId(), fechaInicio, fechaFin);

            BigDecimal debitos = BigDecimal.ZERO;
            BigDecimal creditos = BigDecimal.ZERO;

            List<ReporteDTO.MovimientoReporte> movimientosReporte = new ArrayList<>();
            for (Movimiento mov : movimientos) {
                ReporteDTO.MovimientoReporte movReporte = new ReporteDTO.MovimientoReporte();
                movReporte.setFecha(mov.getFecha());
                movReporte.setTipo(mov.getTipoMovimiento());
                movReporte.setValor(mov.getValor().abs());
                movReporte.setSaldo(mov.getSaldo());

                if (mov.getValor().compareTo(BigDecimal.ZERO) < 0) {
                    debitos = debitos.add(mov.getValor().abs());
                } else {
                    creditos = creditos.add(mov.getValor());
                }

                movimientosReporte.add(movReporte);
            }

            cuentaReporte.setMovimientos(movimientosReporte);
            cuentaReporte.setTotalDebitos(debitos);
            cuentaReporte.setTotalCreditos(creditos);

            totalDebitos = totalDebitos.add(debitos);
            totalCreditos = totalCreditos.add(creditos);

            cuentasReporte.add(cuentaReporte);
        }

        reporte.setCuentas(cuentasReporte);
        reporte.setTotalDebitos(totalDebitos);
        reporte.setTotalCreditos(totalCreditos);

        return reporte;
    }
}
