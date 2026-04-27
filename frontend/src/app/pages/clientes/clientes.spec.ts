import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest';
import { ClientesComponent } from './clientes';
import { ClienteService, Cliente } from '../../services/cliente.service';

describe('ClientesComponent', () => {
  let component: ClientesComponent;
  let clienteServiceMock: any;

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
    },
    {
      personaId: 2,
      nombre: 'Marcos Antonio',
      genero: 'M',
      edad: 25,
      identificacion: '654321',
      direccion: 'Calle 2',
      telefono: '5559876543',
      contrasena: 'pass456',
      estado: true
    }
  ];

  beforeEach(() => {
    const cdrMock = { detectChanges: vi.fn() };

    clienteServiceMock = {
      obtenerTodos: vi.fn(() => ({
        subscribe: ({ next }: any) => next(mockClientes)
      })),
      eliminar: vi.fn(() => ({
        subscribe: ({ next }: any) => next(null)
      }))
    };

    component = new ClientesComponent(clienteServiceMock, cdrMock as any);
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  describe('ngOnInit', () => {
    it('should load clientes on initialization', () => {
      component.ngOnInit();

      expect(clienteServiceMock.obtenerTodos).toHaveBeenCalled();
      expect(component.clientes.length).toBe(2);
      expect(component.clientesFiltrados.length).toBe(2);
    });
  });

  describe('cargarClientes', () => {
    it('should fetch and set clientes', () => {
      component.cargarClientes();

      expect(clienteServiceMock.obtenerTodos).toHaveBeenCalled();
      expect(component.clientes).toEqual(mockClientes);
    });

    it('should filter clientes after loading', () => {
      component.cargarClientes();

      expect(component.clientesFiltrados).toEqual(mockClientes);
    });
  });

  describe('filtrar', () => {
    beforeEach(() => {
      component.clientes = mockClientes;
    });

    it('should return all clientes when search is empty', () => {
      component.busqueda = '';
      component.filtrar();

      expect(component.clientesFiltrados.length).toBe(2);
    });

    it('should filter by nombre', () => {
      component.busqueda = 'Juan';
      component.filtrar();

      expect(component.clientesFiltrados.length).toBe(1);
      expect(component.clientesFiltrados[0].nombre).toBe('Juan Pérez');
    });

    it('should filter by identificacion', () => {
      component.busqueda = '123456';
      component.filtrar();

      expect(component.clientesFiltrados.length).toBe(1);
      expect(component.clientesFiltrados[0].identificacion).toBe('123456');
    });

    it('should be case insensitive', () => {
      component.busqueda = 'juan';
      component.filtrar();

      expect(component.clientesFiltrados.length).toBe(1);
    });
  });

  describe('nuevoCliente', () => {
    it('should open form for new client', () => {
      component.nuevoCliente();

      expect(component.mostraFormulario).toBe(true);
      expect(component.clienteEditando).toBeNull();
    });
  });

  describe('editar', () => {
    it('should open form for editing client', () => {
      const clienteToEdit = mockClientes[0];
      component.editar(clienteToEdit);

      expect(component.mostraFormulario).toBe(true);
      expect(component.clienteEditando).toEqual(clienteToEdit);
    });
  });

  describe('eliminar', () => {
    it('should delete cliente when confirmed', () => {
      vi.spyOn(window, 'confirm').mockReturnValue(true);
      component.clientes = mockClientes;

      component.eliminar(mockClientes[0]);

      expect(clienteServiceMock.eliminar).toHaveBeenCalledWith(1);
    });

    it('should not delete when not confirmed', () => {
      vi.spyOn(window, 'confirm').mockReturnValue(false);

      component.eliminar(mockClientes[0]);

      expect(clienteServiceMock.eliminar).not.toHaveBeenCalled();
    });
  });

  describe('cerrarFormulario', () => {
    it('should close the form', () => {
      component.mostraFormulario = true;
      component.cerrarFormulario();

      expect(component.mostraFormulario).toBe(false);
    });
  });

  describe('mostrarExito', () => {
    it('should set success message and clear after timeout', () => {
      vi.useFakeTimers();
      component.mostrarExito('Test message');

      expect(component.mensaje).toBe('Test message');
      expect(component.tipoMensaje).toBe('exito');

      vi.advanceTimersByTime(3000);
      expect(component.mensaje).toBe('');
    });
  });

  describe('mostrarError', () => {
    it('should set error message and clear after timeout', () => {
      vi.useFakeTimers();
      component.mostrarError('Error message');

      expect(component.mensaje).toBe('Error message');
      expect(component.tipoMensaje).toBe('error');

      vi.advanceTimersByTime(3000);
      expect(component.mensaje).toBe('');
    });
  });
});
