package com.bank.devsu.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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

import com.bank.devsu.dto.cuenta.CuentaRequest;
import com.bank.devsu.dto.cuenta.CuentaResponse;
import com.bank.devsu.entity.Cliente;
import com.bank.devsu.entity.Cuenta;
import com.bank.devsu.enums.TipoCuenta;
import com.bank.devsu.mapper.CuentaMapper;
import com.bank.devsu.service.CuentaService;

public class CuentaControllerTests {

    @Mock
    private CuentaService cuentaService;

    @Mock
    private CuentaMapper cuentaMapper;

    @InjectMocks
    private CuentaController cuentaController;

    private Cliente cliente;
    private Cuenta cuenta;
    private CuentaRequest cuentaRequest;
    private CuentaResponse cuentaResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        cliente = new Cliente("Jose Lema", "M", 45, "0950576829", "Otavalo sn y principal", "098254785", "1234", true);
        cliente.setPersonaId(1L);

        cuenta = new Cuenta("478758", TipoCuenta.AHORROS, new BigDecimal("2000"), true, cliente);
        cuenta.setCuentaId(1L);
        cuenta.setSaldoDisponible(new BigDecimal("2000"));
        cuenta.setLimiteDiarioRetiro(new BigDecimal("1000"));

        cuentaRequest = new CuentaRequest();
        cuentaRequest.setNumeroCuenta("478758");
        cuentaRequest.setTipoCuenta(TipoCuenta.AHORROS);
        cuentaRequest.setSaldoInicial(new BigDecimal("2000"));
        cuentaRequest.setEstado(true);
        cuentaRequest.setPersonaId(1L);

        cuentaResponse = new CuentaResponse();
        cuentaResponse.setCuentaId(1L);
        cuentaResponse.setNumeroCuenta("478758");
        cuentaResponse.setTipoCuenta(TipoCuenta.AHORROS);
        cuentaResponse.setSaldoInicial(new BigDecimal("2000"));
        cuentaResponse.setSaldoDisponible(new BigDecimal("2000"));
        cuentaResponse.setEstado(true);
        cuentaResponse.setLimiteDiarioRetiro(new BigDecimal("1000"));
    }

    // ─── GET /cuentas ────────────────────────────────────────────────────────────

    @Test
    public void testObtenerTodas_RetornaLista() {
        List<Cuenta> cuentas = List.of(cuenta);
        List<CuentaResponse> responses = List.of(cuentaResponse);

        when(cuentaService.obtenerTodas()).thenReturn(cuentas);
        when(cuentaMapper.toResponseList(cuentas)).thenReturn(responses);

        ResponseEntity<List<CuentaResponse>> response = cuentaController.obtenerTodas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(cuentaService, times(1)).obtenerTodas();
    }

    @Test
    public void testObtenerTodas_ListaVacia() {
        when(cuentaService.obtenerTodas()).thenReturn(Collections.emptyList());
        when(cuentaMapper.toResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        ResponseEntity<List<CuentaResponse>> response = cuentaController.obtenerTodas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    // ─── GET /cuentas/{cuentaId} ─────────────────────────────────────────────────

    @Test
    public void testObtenerPorId_Encontrada() {
        when(cuentaService.obtenerPorId(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaMapper.toResponse(cuenta)).thenReturn(cuentaResponse);

        ResponseEntity<CuentaResponse> response = cuentaController.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("478758", response.getBody().getNumeroCuenta());
        verify(cuentaService, times(1)).obtenerPorId(1L);
    }

    @Test
    public void testObtenerPorId_NoEncontrada() {
        when(cuentaService.obtenerPorId(999L)).thenReturn(Optional.empty());

        ResponseEntity<CuentaResponse> response = cuentaController.obtenerPorId(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ─── GET /cuentas/numero/{numeroCuenta} ──────────────────────────────────────

    @Test
    public void testObtenerPorNumeroCuenta_Encontrada() {
        when(cuentaService.obtenerPorNumeroCuenta("478758")).thenReturn(Optional.of(cuenta));
        when(cuentaMapper.toResponse(cuenta)).thenReturn(cuentaResponse);

        ResponseEntity<CuentaResponse> response = cuentaController.obtenerPorNumeroCuenta("478758");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(TipoCuenta.AHORROS, response.getBody().getTipoCuenta());
        verify(cuentaService, times(1)).obtenerPorNumeroCuenta("478758");
    }

    @Test
    public void testObtenerPorNumeroCuenta_NoEncontrada() {
        when(cuentaService.obtenerPorNumeroCuenta("000000")).thenReturn(Optional.empty());

        ResponseEntity<CuentaResponse> response = cuentaController.obtenerPorNumeroCuenta("000000");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ─── GET /cuentas/cliente/{personaId} ────────────────────────────────────────

    @Test
    public void testObtenerPorClienteId_ConCuentas() {
        List<Cuenta> cuentas = List.of(cuenta);
        List<CuentaResponse> responses = List.of(cuentaResponse);

        when(cuentaService.obtenerPorClienteId(1L)).thenReturn(cuentas);
        when(cuentaMapper.toResponseList(cuentas)).thenReturn(responses);

        ResponseEntity<List<CuentaResponse>> response = cuentaController.obtenerPorClienteId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(cuentaService, times(1)).obtenerPorClienteId(1L);
    }

    @Test
    public void testObtenerPorClienteId_SinCuentas() {
        when(cuentaService.obtenerPorClienteId(999L)).thenReturn(Collections.emptyList());
        when(cuentaMapper.toResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        ResponseEntity<List<CuentaResponse>> response = cuentaController.obtenerPorClienteId(999L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    // ─── GET /cuentas/buscar?numeroCuenta= ───────────────────────────────────────

    @Test
    public void testBuscar_ConResultados() {
        List<Cuenta> cuentas = List.of(cuenta);
        List<CuentaResponse> responses = List.of(cuentaResponse);

        when(cuentaService.buscarPorNumeroCuenta("478")).thenReturn(cuentas);
        when(cuentaMapper.toResponseList(cuentas)).thenReturn(responses);

        ResponseEntity<List<CuentaResponse>> response = cuentaController.buscar("478");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testBuscar_SinResultados() {
        when(cuentaService.buscarPorNumeroCuenta("999")).thenReturn(Collections.emptyList());
        when(cuentaMapper.toResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        ResponseEntity<List<CuentaResponse>> response = cuentaController.buscar("999");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    // ─── POST /cuentas ───────────────────────────────────────────────────────────

    @Test
    public void testCrear_Exitosa() {
        when(cuentaMapper.toEntity(any(CuentaRequest.class))).thenReturn(cuenta);
        when(cuentaService.crear(any(Cuenta.class), anyLong())).thenReturn(cuenta);
        when(cuentaMapper.toResponse(cuenta)).thenReturn(cuentaResponse);

        ResponseEntity<?> response = cuentaController.crear(cuentaRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(cuentaService, times(1)).crear(any(Cuenta.class), anyLong());
    }

    @Test
    public void testCrear_NumeroCuentaDuplicado() {
        when(cuentaMapper.toEntity(any(CuentaRequest.class))).thenReturn(cuenta);
        when(cuentaService.crear(any(Cuenta.class), anyLong()))
                .thenThrow(new IllegalArgumentException("El número de cuenta ya existe"));

        ResponseEntity<?> response = cuentaController.crear(cuentaRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El número de cuenta ya existe", response.getBody());
    }

    // ─── PUT /cuentas/{cuentaId} ─────────────────────────────────────────────────

    @Test
    public void testActualizar_Exitosa() {
        when(cuentaMapper.toEntity(any(CuentaRequest.class))).thenReturn(cuenta);
        when(cuentaService.actualizar(anyLong(), any(Cuenta.class))).thenReturn(cuenta);
        when(cuentaMapper.toResponse(cuenta)).thenReturn(cuentaResponse);

        ResponseEntity<?> response = cuentaController.actualizar(1L, cuentaRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(cuentaService, times(1)).actualizar(anyLong(), any(Cuenta.class));
    }

    @Test
    public void testActualizar_NoEncontrada() {
        when(cuentaMapper.toEntity(any(CuentaRequest.class))).thenReturn(cuenta);
        when(cuentaService.actualizar(anyLong(), any(Cuenta.class)))
                .thenThrow(new IllegalArgumentException("Cuenta no encontrada"));

        ResponseEntity<?> response = cuentaController.actualizar(999L, cuentaRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ─── DELETE /cuentas/{cuentaId} ──────────────────────────────────────────────

    @Test
    public void testEliminar_Exitosa() {
        doNothing().when(cuentaService).eliminar(1L);

        ResponseEntity<Void> response = cuentaController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cuentaService, times(1)).eliminar(1L);
    }

    @Test
    public void testEliminar_NoEncontrada() {
        doThrow(new IllegalArgumentException("Cuenta no encontrada")).when(cuentaService).eliminar(999L);

        ResponseEntity<Void> response = cuentaController.eliminar(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
