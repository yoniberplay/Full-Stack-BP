import { describe, it, expect, beforeEach, vi } from 'vitest';
import { ClienteService, Cliente } from './cliente.service';
import { HttpClient } from '@angular/common/http';

describe('ClienteService', () => {
  let service: ClienteService;
  let httpClientMock: any;

  beforeEach(() => {
    httpClientMock = {
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      delete: vi.fn()
    };

    service = new ClienteService(httpClientMock);
  });

  describe('obtenerTodos', () => {
    it('should fetch all clientes', () => {
      const mockClientes: Cliente[] = [
        {
          personaId: 1,
          nombre: 'Juan Pérez',
          genero: 'M',
          edad: 30,
          identificacion: '123456',
          direccion: 'Calle 1',
          telefono: '5551234567',
          contrasena: 'pass123',
          estado: true
        }
      ];

      httpClientMock.get.mockReturnValue({
        subscribe: (callback: any) => callback(mockClientes)
      });

      service.obtenerTodos().subscribe((data) => {
        expect(data).toEqual(mockClientes);
        expect(httpClientMock.get).toHaveBeenCalledWith('http://localhost:5001/clientes');
      });
    });

    it('should call the correct API endpoint', () => {
      httpClientMock.get.mockReturnValue({
        subscribe: () => {}
      });

      service.obtenerTodos();

      expect(httpClientMock.get).toHaveBeenCalledWith('http://localhost:5001/clientes');
    });
  });

  describe('obtenerPorId', () => {
    it('should fetch cliente by id', () => {
      const mockCliente: Cliente = {
        personaId: 1,
        nombre: 'Juan Pérez',
        genero: 'M',
        edad: 30,
        identificacion: '123456',
        direccion: 'Calle 1',
        telefono: '5551234567',
        contrasena: 'pass123',
        estado: true
      };

      httpClientMock.get.mockReturnValue({
        subscribe: (callback: any) => callback(mockCliente)
      });

      service.obtenerPorId(1).subscribe((data) => {
        expect(data).toEqual(mockCliente);
      });
    });
  });

  describe('crear', () => {
    it('should create a new cliente', () => {
      const newCliente: Cliente = {
        nombre: 'Jane Doe',
        genero: 'F',
        edad: 25,
        identificacion: '654321',
        direccion: 'Calle 2',
        telefono: '5559876543',
        contrasena: 'pass456',
        estado: true
      };

      httpClientMock.post.mockReturnValue({
        subscribe: (callback: any) => callback({ ...newCliente, personaId: 2 })
      });

      service.crear(newCliente).subscribe((data) => {
        expect(data.nombre).toBe(newCliente.nombre);
        expect(httpClientMock.post).toHaveBeenCalled();
      });
    });
  });

  describe('actualizar', () => {
    it('should update an existing cliente', () => {
      const updatedCliente: Cliente = {
        personaId: 1,
        nombre: 'Juan Actualizado',
        genero: 'M',
        edad: 31,
        identificacion: '123456',
        direccion: 'Calle 1 Actualizada',
        telefono: '5551234567',
        contrasena: 'newpass',
        estado: true
      };

      httpClientMock.put.mockReturnValue({
        subscribe: (callback: any) => callback(updatedCliente)
      });

      service.actualizar(1, updatedCliente).subscribe((data) => {
        expect(data.nombre).toBe('Juan Actualizado');
        expect(httpClientMock.put).toHaveBeenCalledWith(
          'http://localhost:5001/clientes/1',
          updatedCliente
        );
      });
    });
  });

  describe('eliminar', () => {
    it('should delete a cliente', () => {
      httpClientMock.delete.mockReturnValue({
        subscribe: (callback: any) => callback(null)
      });

      service.eliminar(1).subscribe(() => {
        expect(httpClientMock.delete).toHaveBeenCalledWith('http://localhost:5001/clientes/1');
      });
    });
  });

  describe('buscar', () => {
    it('should search clientes by name', () => {
      const mockClientes: Cliente[] = [
        {
          personaId: 1,
          nombre: 'Juan Pérez',
          genero: 'M',
          edad: 30,
          identificacion: '123456',
          direccion: 'Calle 1',
          telefono: '5551234567',
          contrasena: 'pass123',
          estado: true
        }
      ];

      httpClientMock.get.mockReturnValue({
        subscribe: (callback: any) => callback(mockClientes)
      });

      service.buscar('Juan').subscribe((data) => {
        expect(data).toEqual(mockClientes);
      });
    });
  });
});
