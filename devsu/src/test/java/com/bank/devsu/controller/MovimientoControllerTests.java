package com.bank.devsu.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bank.devsu.dto.movimiento.MovimientoRequest;
import com.bank.devsu.dto.movimiento.MovimientoResponse;
import com.bank.devsu.entity.Cliente;
import com.bank.devsu.entity.Cuenta;
import com.bank.devsu.entity.Movimiento;
import com.bank.devsu.enums.TipoCuenta;
import com.bank.devsu.mapper.MovimientoMapper;
import com.bank.devsu.service.CuentaService;
import com.bank.devsu.service.MovimientoService;

public class MovimientoControllerTests {

    @Mock
    private MovimientoService movimientoService;

    @Mock
    private CuentaService cuentaService;

    @Mock
    private MovimientoMapper movimientoMapper;

    @InjectMocks
    private MovimientoController movimientoController;

    private Cliente cliente;
    private Cuenta cuenta;
    private Movimiento movimiento;
    private MovimientoResponse movimientoResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        cliente = new Cliente("Jose Lema", "M", 30, "1234567890", "Otavalo", "098254785", "1234", true);
        cliente.setPersonaId(1L);

        cuenta = new Cuenta("478758", TipoCuenta.AHORROS, new BigDecimal("2000"), true, cliente);
        cuenta.setCuentaId(1L);

        movimiento = new Movimiento(LocalDateTime.now(), "Retiro", new BigDecimal("500"), new BigDecimal("1500"), cuenta);
        movimiento.setMovimientoId(1L);

        movimientoResponse = new MovimientoResponse();
        movimientoResponse.setMovimientoId(1L);
        movimientoResponse.setTipoMovimiento("Retiro");
        movimientoResponse.setValor(new BigDecimal("500"));
        movimientoResponse.setSaldo(new BigDecimal("1500"));
        movimientoResponse.setCuentaId(1L);
    }

    // ─── GET /movimientos ────────────────────────────────────────────────────────

    @Test
    public void testObtenerTodos_RetornaLista() {
        List<Movimiento> movimientos = List.of(movimiento);
        List<MovimientoResponse> responseList = List.of(movimientoResponse);

        when(movimientoService.obtenerTodos()).thenReturn(movimientos);
        when(movimientoMapper.toResponseList(movimientos)).thenReturn(responseList);

        ResponseEntity<List<MovimientoResponse>> response = movimientoController.obtenerTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(movimientoService, times(1)).obtenerTodos();
    }

    @Test
    public void testObtenerTodos_ListaVacia() {
        when(movimientoService.obtenerTodos()).thenReturn(Collections.emptyList());
        when(movimientoMapper.toResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        ResponseEntity<List<MovimientoResponse>> response = movimientoController.obtenerTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    // ─── GET /movimientos/{movimientoId} ─────────────────────────────────────────

    @Test
    public void testObtenerPorId_Encontrado() {
        when(movimientoService.obtenerPorId(1L)).thenReturn(Optional.of(movimiento));
        when(movimientoMapper.toResponse(movimiento)).thenReturn(movimientoResponse);

        ResponseEntity<MovimientoResponse> response = movimientoController.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getMovimientoId());
        verify(movimientoService, times(1)).obtenerPorId(1L);
    }

    @Test
    public void testObtenerPorId_NoEncontrado() {
        when(movimientoService.obtenerPorId(999L)).thenReturn(Optional.empty());

        ResponseEntity<MovimientoResponse> response = movimientoController.obtenerPorId(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ─── GET /movimientos/cuenta/{cuentaId} ──────────────────────────────────────

    @Test
    public void testObtenerPorCuentaId_ConMovimientos() {
        List<Movimiento> movimientos = List.of(movimiento);
        List<MovimientoResponse> responseList = List.of(movimientoResponse);

        when(movimientoService.obtenerPorCuentaId(1L)).thenReturn(movimientos);
        when(movimientoMapper.toResponseList(movimientos)).thenReturn(responseList);

        ResponseEntity<List<MovimientoResponse>> response = movimientoController.obtenerPorCuentaId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(movimientoService, times(1)).obtenerPorCuentaId(1L);
    }

    @Test
    public void testObtenerPorCuentaId_SinMovimientos() {
        when(movimientoService.obtenerPorCuentaId(999L)).thenReturn(Collections.emptyList());
        when(movimientoMapper.toResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        ResponseEntity<List<MovimientoResponse>> response = movimientoController.obtenerPorCuentaId(999L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    // ─── POST /movimientos ───────────────────────────────────────────────────────

    @Test
    public void testCrear_Exitoso() {
        MovimientoRequest movimientoRequest = new MovimientoRequest();
        movimientoRequest.setTipoMovimiento("Retiro");
        movimientoRequest.setValor(new BigDecimal("500"));
        movimientoRequest.setCuentaId(1L);

        when(cuentaService.obtenerPorId(1L)).thenReturn(Optional.of(cuenta));
        when(movimientoMapper.toEntity(any(MovimientoRequest.class))).thenReturn(movimiento);
        when(movimientoService.crear(any(Movimiento.class))).thenReturn(movimiento);
        when(movimientoMapper.toResponse(movimiento)).thenReturn(movimientoResponse);

        ResponseEntity<?> response = movimientoController.crear(movimientoRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(movimientoService, times(1)).crear(any(Movimiento.class));
    }

    @Test
    public void testCrear_CuentaNoEncontrada() {
        MovimientoRequest movimientoRequest = new MovimientoRequest();
        movimientoRequest.setCuentaId(999L);

        when(cuentaService.obtenerPorId(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = movimientoController.crear(movimientoRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Cuenta no encontrada"));
    }

    // ─── DELETE /movimientos/{movimientoId} ──────────────────────────────────────

    @Test
    public void testEliminar_Exitoso() {
        doNothing().when(movimientoService).eliminar(1L);

        ResponseEntity<Void> response = movimientoController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(movimientoService, times(1)).eliminar(1L);
    }

    @Test
    public void testEliminar_NoEncontrado() {
        doThrow(new IllegalArgumentException("Movimiento no encontrado")).when(movimientoService).eliminar(999L);

        ResponseEntity<Void> response = movimientoController.eliminar(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
