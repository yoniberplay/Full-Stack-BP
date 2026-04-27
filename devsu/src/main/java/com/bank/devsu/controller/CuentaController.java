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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.devsu.dto.cuenta.CuentaRequest;
import com.bank.devsu.dto.cuenta.CuentaResponse;
import com.bank.devsu.entity.Cuenta;
import com.bank.devsu.mapper.CuentaMapper;
import com.bank.devsu.service.CuentaService;

@RestController
@RequestMapping("/cuentas")
@CrossOrigin(origins = "*")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private CuentaMapper cuentaMapper;

    @GetMapping
    public ResponseEntity<List<CuentaResponse>> obtenerTodas() {
        return ResponseEntity.ok(cuentaMapper.toResponseList(cuentaService.obtenerTodas()));
    }

    @GetMapping("/{cuentaId}")
    public ResponseEntity<CuentaResponse> obtenerPorId(@PathVariable Long cuentaId) {
        Optional<Cuenta> cuenta = cuentaService.obtenerPorId(cuentaId);
        return cuenta.map(c -> ResponseEntity.ok(cuentaMapper.toResponse(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/numero/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> obtenerPorNumeroCuenta(@PathVariable String numeroCuenta) {
        Optional<Cuenta> cuenta = cuentaService.obtenerPorNumeroCuenta(numeroCuenta);
        return cuenta.map(c -> ResponseEntity.ok(cuentaMapper.toResponse(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{personaId}")
    public ResponseEntity<List<CuentaResponse>> obtenerPorClienteId(@PathVariable Long personaId) {
        return ResponseEntity.ok(cuentaMapper.toResponseList(cuentaService.obtenerPorClienteId(personaId)));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<CuentaResponse>> buscar(@RequestParam String numeroCuenta) {
        return ResponseEntity.ok(cuentaMapper.toResponseList(cuentaService.buscarPorNumeroCuenta(numeroCuenta)));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CuentaRequest cuentaRequest) {
        try {
            Cuenta cuenta = cuentaMapper.toEntity(cuentaRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(cuentaMapper.toResponse(cuentaService.crear(cuenta, cuentaRequest.getPersonaId())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{cuentaId}")
    public ResponseEntity<?> actualizar(@PathVariable Long cuentaId, @RequestBody CuentaRequest cuentaRequest) {
        try {
            Cuenta cuenta = cuentaMapper.toEntity(cuentaRequest);
            return ResponseEntity.ok(cuentaMapper.toResponse(cuentaService.actualizar(cuentaId, cuenta)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{cuentaId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long cuentaId) {
        try {
            cuentaService.eliminar(cuentaId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
