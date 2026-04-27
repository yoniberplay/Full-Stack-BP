package com.bank.devsu.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.bank.devsu.dto.cliente.ClienteRequest;
import com.bank.devsu.dto.cliente.ClienteResponse;
import com.bank.devsu.entity.Cliente;
import com.bank.devsu.mapper.ClienteMapper;
import com.bank.devsu.service.ClienteService;

public class ClienteControllerTests {

    @Mock
    private ClienteService clienteService;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteController clienteController;

    private Cliente cliente;
    private ClienteRequest clienteRequest;
    private ClienteResponse clienteResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        cliente = new Cliente("Jose Lema", "M", 45, "0950576829", "Otavalo sn y principal", "098254785", "1234", true);
        cliente.setPersonaId(1L);

        clienteRequest = new ClienteRequest();
        clienteRequest.setNombre("Jose Lema");
        clienteRequest.setGenero("M");
        clienteRequest.setEdad(45);
        clienteRequest.setIdentificacion("0950576829");
        clienteRequest.setDireccion("Otavalo sn y principal");
        clienteRequest.setTelefono("098254785");
        clienteRequest.setContrasena("1234");
        clienteRequest.setEstado(true);

        clienteResponse = new ClienteResponse();
        clienteResponse.setPersonaId(1L);
        clienteResponse.setNombre("Jose Lema");
        clienteResponse.setGenero("M");
        clienteResponse.setEdad(45);
        clienteResponse.setIdentificacion("0950576829");
        clienteResponse.setDireccion("Otavalo sn y principal");
        clienteResponse.setTelefono("098254785");
        clienteResponse.setEstado(true);
    }

    // ─── GET /clientes ───────────────────────────────────────────────────────────

    @Test
    public void testObtenerTodos_RetornaLista() {
        List<Cliente> clientes = List.of(cliente);
        List<ClienteResponse> responses = List.of(clienteResponse);

        when(clienteService.obtenerTodos()).thenReturn(clientes);
        when(clienteMapper.toResponseList(clientes)).thenReturn(responses);

        ResponseEntity<List<ClienteResponse>> response = clienteController.obtenerTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(clienteService, times(1)).obtenerTodos();
    }

    @Test
    public void testObtenerTodos_ListaVacia() {
        when(clienteService.obtenerTodos()).thenReturn(Collections.emptyList());
        when(clienteMapper.toResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        ResponseEntity<List<ClienteResponse>> response = clienteController.obtenerTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    // ─── GET /clientes/{clienteId} ───────────────────────────────────────────────

    @Test
    public void testObtenerPorId_Encontrado() {
        when(clienteService.obtenerPorId(1L)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toResponse(cliente)).thenReturn(clienteResponse);

        ResponseEntity<ClienteResponse> response = clienteController.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getPersonaId());
        verify(clienteService, times(1)).obtenerPorId(1L);
    }

    @Test
    public void testObtenerPorId_NoEncontrado() {
        when(clienteService.obtenerPorId(999L)).thenReturn(Optional.empty());

        ResponseEntity<ClienteResponse> response = clienteController.obtenerPorId(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ─── GET /clientes/buscar?nombre= ────────────────────────────────────────────

    @Test
    public void testBuscar_ConResultados() {
        List<Cliente> clientes = List.of(cliente);
        List<ClienteResponse> responses = List.of(clienteResponse);

        when(clienteService.buscarPorNombre("Jose")).thenReturn(clientes);
        when(clienteMapper.toResponseList(clientes)).thenReturn(responses);

        ResponseEntity<List<ClienteResponse>> response = clienteController.buscar("Jose");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(clienteService, times(1)).buscarPorNombre("Jose");
    }

    @Test
    public void testBuscar_SinResultados() {
        when(clienteService.buscarPorNombre(anyString())).thenReturn(Collections.emptyList());
        when(clienteMapper.toResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        ResponseEntity<List<ClienteResponse>> response = clienteController.buscar("Inexistente");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    // ─── POST /clientes ──────────────────────────────────────────────────────────

    @Test
    public void testCrear_Exitoso() {
        when(clienteMapper.toEntity(any(ClienteRequest.class))).thenReturn(cliente);
        when(clienteService.crear(any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toResponse(cliente)).thenReturn(clienteResponse);

        ResponseEntity<ClienteResponse> response = clienteController.crear(clienteRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Jose Lema", response.getBody().getNombre());
        verify(clienteService, times(1)).crear(any(Cliente.class));
    }

    @Test
    public void testCrear_IdentificacionDuplicada() {
        when(clienteMapper.toEntity(any(ClienteRequest.class))).thenReturn(cliente);
        when(clienteService.crear(any(Cliente.class)))
                .thenThrow(new IllegalArgumentException("La identificación ya existe"));

        ResponseEntity<ClienteResponse> response = clienteController.crear(clienteRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ─── PUT /clientes/{clienteId} ───────────────────────────────────────────────

    @Test
    public void testActualizar_Exitoso() {
        when(clienteMapper.toEntity(any(ClienteRequest.class))).thenReturn(cliente);
        when(clienteService.actualizar(anyLong(), any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toResponse(cliente)).thenReturn(clienteResponse);

        ResponseEntity<ClienteResponse> response = clienteController.actualizar(1L, clienteRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(clienteService, times(1)).actualizar(anyLong(), any(Cliente.class));
    }

    @Test
    public void testActualizar_NoEncontrado() {
        when(clienteMapper.toEntity(any(ClienteRequest.class))).thenReturn(cliente);
        when(clienteService.actualizar(anyLong(), any(Cliente.class)))
                .thenThrow(new IllegalArgumentException("Cliente no encontrado"));

        ResponseEntity<ClienteResponse> response = clienteController.actualizar(999L, clienteRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ─── DELETE /clientes/{clienteId} ────────────────────────────────────────────

    @Test
    public void testEliminar_Exitoso() {
        doNothing().when(clienteService).eliminar(1L);

        ResponseEntity<Void> response = clienteController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(clienteService, times(1)).eliminar(1L);
    }

    @Test
    public void testEliminar_NoEncontrado() {
        doThrow(new IllegalArgumentException("Cliente no encontrado")).when(clienteService).eliminar(999L);

        ResponseEntity<Void> response = clienteController.eliminar(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
