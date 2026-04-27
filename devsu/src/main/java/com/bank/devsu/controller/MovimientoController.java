package com.bank.devsu.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.devsu.dto.movimiento.MovimientoRequest;
import com.bank.devsu.dto.movimiento.MovimientoResponse;
import com.bank.devsu.entity.Cuenta;
import com.bank.devsu.entity.Movimiento;
import com.bank.devsu.mapper.MovimientoMapper;
import com.bank.devsu.service.CuentaService;
import com.bank.devsu.service.MovimientoService;

@RestController
@RequestMapping("/movimientos")
@CrossOrigin(origins = "*")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private MovimientoMapper movimientoMapper;

    @GetMapping
    public ResponseEntity<List<MovimientoResponse>> obtenerTodos() {
        return ResponseEntity.ok(movimientoMapper.toResponseList(movimientoService.obtenerTodos()));
    }

    @GetMapping("/{movimientoId}")
    public ResponseEntity<MovimientoResponse> obtenerPorId(@PathVariable Long movimientoId) {
        Optional<Movimiento> movimiento = movimientoService.obtenerPorId(movimientoId);
        return movimiento.map(m -> ResponseEntity.ok(movimientoMapper.toResponse(m)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<MovimientoResponse>> obtenerPorCuentaId(@PathVariable Long cuentaId) {
        return ResponseEntity.ok(movimientoMapper.toResponseList(movimientoService.obtenerPorCuentaId(cuentaId)));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody MovimientoRequest movimientoRequest) {
        try {
            Optional<Cuenta> cuentaOpt = cuentaService.obtenerPorId(movimientoRequest.getCuentaId());
            if (cuentaOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Cuenta no encontrada");
            }

            Movimiento movimiento = movimientoMapper.toEntity(movimientoRequest);
            movimiento.setCuenta(cuentaOpt.get());

            Movimiento movimientoCreado = movimientoService.crear(movimiento);
            return ResponseEntity.status(HttpStatus.CREATED).body(movimientoMapper.toResponse(movimientoCreado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{movimientoId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long movimientoId) {
        try {
            movimientoService.eliminar(movimientoId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
