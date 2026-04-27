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

import com.bank.devsu.dto.cliente.ClienteRequest;
import com.bank.devsu.dto.cliente.ClienteResponse;
import com.bank.devsu.entity.Cliente;
import com.bank.devsu.mapper.ClienteMapper;
import com.bank.devsu.service.ClienteService;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteMapper clienteMapper;

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> obtenerTodos() {
        return ResponseEntity.ok(clienteMapper.toResponseList(clienteService.obtenerTodos()));
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<ClienteResponse> obtenerPorId(@PathVariable Long clienteId) {
        Optional<Cliente> cliente = clienteService.obtenerPorId(clienteId);
        return cliente.map(c -> ResponseEntity.ok(clienteMapper.toResponse(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteResponse>> buscar(@RequestParam String nombre) {
        return ResponseEntity.ok(clienteMapper.toResponseList(clienteService.buscarPorNombre(nombre)));
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@RequestBody ClienteRequest clienteRequest) {
        try {
            Cliente cliente = clienteMapper.toEntity(clienteRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteMapper.toResponse(clienteService.crear(cliente)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{clienteId}")
    public ResponseEntity<ClienteResponse> actualizar(@PathVariable Long clienteId, @RequestBody ClienteRequest clienteRequest) {
        try {
            Cliente cliente = clienteMapper.toEntity(clienteRequest);
            return ResponseEntity.ok(clienteMapper.toResponse(clienteService.actualizar(clienteId, cliente)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long clienteId) {
        try {
            clienteService.eliminar(clienteId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
