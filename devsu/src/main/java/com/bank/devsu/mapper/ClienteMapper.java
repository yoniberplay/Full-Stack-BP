package com.bank.devsu.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bank.devsu.dto.cliente.ClienteRequest;
import com.bank.devsu.dto.cliente.ClienteResponse;
import com.bank.devsu.entity.Cliente;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequest request) {
        if (request == null) {
			return null;
		}
        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setGenero(request.getGenero());
        cliente.setEdad(request.getEdad());
        cliente.setIdentificacion(request.getIdentificacion());
        cliente.setDireccion(request.getDireccion());
        cliente.setTelefono(request.getTelefono());
        cliente.setContrasena(request.getContrasena());
        cliente.setEstado(request.getEstado());
        return cliente;
    }

    public ClienteResponse toResponse(Cliente cliente) {
        if (cliente == null) {
			return null;
		}
        ClienteResponse response = new ClienteResponse();
        response.setPersonaId(cliente.getPersonaId());
        response.setNombre(cliente.getNombre());
        response.setGenero(cliente.getGenero());
        response.setEdad(cliente.getEdad());
        response.setIdentificacion(cliente.getIdentificacion());
        response.setDireccion(cliente.getDireccion());
        response.setTelefono(cliente.getTelefono());
        response.setEstado(cliente.getEstado());
        return response;
    }

    public List<ClienteResponse> toResponseList(List<Cliente> clientes) {
        if (clientes == null) {
			return null;
		}
        return clientes.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void updateEntityFromRequest(ClienteRequest request, Cliente cliente) {
        if (request == null) {
			return;
		}
        cliente.setNombre(request.getNombre());
        cliente.setGenero(request.getGenero());
        cliente.setEdad(request.getEdad());
        cliente.setDireccion(request.getDireccion());
        cliente.setTelefono(request.getTelefono());
        cliente.setContrasena(request.getContrasena());
        cliente.setEstado(request.getEstado());
    }
}
