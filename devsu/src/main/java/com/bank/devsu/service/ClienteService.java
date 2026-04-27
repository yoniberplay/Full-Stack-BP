package com.bank.devsu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.devsu.entity.Cliente;
import com.bank.devsu.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> obtenerPorId(Long clienteId) {
        return clienteRepository.findById(clienteId);
    }

    public Optional<Cliente> obtenerPorIdentificacion(String identificacion) {
        return clienteRepository.findByIdentificacion(identificacion);
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Cliente crear(Cliente cliente) {
        if (clienteRepository.findByIdentificacion(cliente.getIdentificacion()).isPresent()) {
            throw new IllegalArgumentException("La identificación ya existe");
        }
        return clienteRepository.save(cliente);
    }

    public Cliente actualizar(Long clienteId, Cliente clienteActualizado) {
        Optional<Cliente> cliente = clienteRepository.findById(clienteId);
        if (cliente.isEmpty()) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }

        Cliente c = cliente.get();
        c.setNombre(clienteActualizado.getNombre());
        c.setGenero(clienteActualizado.getGenero());
        c.setEdad(clienteActualizado.getEdad());
        c.setDireccion(clienteActualizado.getDireccion());
        c.setTelefono(clienteActualizado.getTelefono());
        c.setContrasena(clienteActualizado.getContrasena());
        c.setEstado(clienteActualizado.getEstado());

        return clienteRepository.save(c);
    }

    public void eliminar(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }
        clienteRepository.deleteById(clienteId);
    }
}
