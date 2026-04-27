import { describe, it, expect, beforeEach, vi } from 'vitest';
import { CuentasComponent } from './cuentas';
import { CuentaService, Cuenta } from '../../services/cuenta.service';
import { ClienteService, Cliente } from '../../services/cliente.service';

describe('CuentasComponent', () => {
  let component: CuentasComponent;
  let cuentaServiceMock: any;
  let clienteServiceMock: any;

  const mockCuentas: Cuenta[] = [
    {
      cuentaId: 1,
      numeroCuenta: '1234567890',
      tipoCuenta: 'Ahorros',
      saldoInicial: 1000,
      saldoDisponible: 1500,
      estado: true,
      personaId: 1
    },
    {
      cuentaId: 2,
      numeroCuenta: '0987654321',
      tipoCuenta: 'Corriente',
      saldoInicial: 5000,
      saldoDisponible: 4500,
      estado: true,
      personaId: 2
    }
  ];

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

  beforeEach(() => {
    const cdrMock = { detectChanges: vi.fn() };

    cuentaServiceMock = {
      obtenerTodas: vi.fn(() => ({
        subscribe: ({ next }: any) => next(mockCuentas)
      })),
      crear: vi.fn(() => ({
        subscribe: ({ next }: any) => next({})
      })),
      actualizar: vi.fn(() => ({
        subscribe: ({ next }: any) => next({})
      })),
      eliminar: vi.fn(() => ({
        subscribe: ({ next }: any) => next(null)
      }))
    };

    clienteServiceMock = {
      obtenerTodos: vi.fn(() => ({
        subscribe: ({ next }: any) => next(mockClientes)
      }))
    };

    component = new CuentasComponent(cuentaServiceMock, clienteServiceMock, cdrMock as any);
  });

  describe('ngOnInit', () => {
    it('should load clientes and cuentas on initialization', () => {
      component.ngOnInit();

      expect(clienteServiceMock.obtenerTodos).toHaveBeenCalled();
      expect(cuentaServiceMock.obtenerTodas).toHaveBeenCalled();
      expect(component.clientes.length).toBe(1);
      expect(component.cuentas.length).toBe(2);
    });
  });

  describe('cargarCuentas', () => {
    it('should fetch and set cuentas', () => {
      component.cargarCuentas();

      expect(cuentaServiceMock.obtenerTodas).toHaveBeenCalled();
      expect(component.cuentas).toEqual(mockCuentas);
    });

    it('should filter cuentas after loading', () => {
      component.cargarCuentas();

      expect(component.cuentasFiltradas).toEqual(mockCuentas);
    });
  });

  describe('filtrar', () => {
    beforeEach(() => {
      component.cuentas = mockCuentas;
    });

    it('should return all cuentas when search is empty', () => {
      component.busqueda = '';
      component.filtrar();

      expect(component.cuentasFiltradas.length).toBe(2);
    });

    it('should filter by numeroCuenta', () => {
      component.busqueda = '1234567890';
      component.filtrar();

      expect(component.cuentasFiltradas.length).toBe(1);
      expect(component.cuentasFiltradas[0].numeroCuenta).toBe('1234567890');
    });

    it('should filter by tipoCuenta', () => {
      component.busqueda = 'ahorros';
      component.filtrar();

      expect(component.cuentasFiltradas.length).toBe(1);
      expect(component.cuentasFiltradas[0].tipoCuenta).toBe('Ahorros');
    });

    it('should be case insensitive', () => {
      component.busqueda = 'CORRIENTE';
      component.filtrar();

      expect(component.cuentasFiltradas.length).toBe(1);
    });
  });

  describe('nuevoRegistro', () => {
    it('should open form for new account', () => {
      component.nuevoRegistro();

      expect(component.mostraFormulario).toBe(true);
      expect(component.novaCuenta.estado).toBe(true);
    });
  });

  describe('editar', () => {
    it('should open form for editing account', () => {
      const cuentaToEdit = mockCuentas[0];
      component.editar(cuentaToEdit);

      expect(component.mostraFormulario).toBe(true);
      expect(component.cuentaEditando).toEqual(cuentaToEdit);
    });
  });

  describe('guardar', () => {
    it('should show error when required fields are missing', () => {
      component.novaCuenta = {};
      component.guardar();

      expect(component.mensaje).toBe('Completa todos los campos');
      expect(component.tipoMensaje).toBe('error');
    });

    it('should create new cuenta when valid', () => {
      component.cuentaEditando = null;
      component.novaCuenta = {
        numeroCuenta: '9999999999',
        tipoCuenta: 'Ahorros',
        personaId: 1,
        estado: true
      };

      component.guardar();

      expect(cuentaServiceMock.crear).toHaveBeenCalled();
    });

    it('should update cuenta when editing', () => {
      component.cuentaEditando = mockCuentas[0];
      component.novaCuenta = { ...mockCuentas[0] };

      component.guardar();

      expect(cuentaServiceMock.actualizar).toHaveBeenCalled();
    });
  });

  describe('eliminar', () => {
    it('should delete cuenta when confirmed', () => {
      vi.spyOn(window, 'confirm').mockReturnValue(true);
      component.cuentas = mockCuentas;

      component.eliminar(mockCuentas[0]);

      expect(cuentaServiceMock.eliminar).toHaveBeenCalledWith(1);
    });

    it('should not delete when not confirmed', () => {
      vi.spyOn(window, 'confirm').mockReturnValue(false);

      component.eliminar(mockCuentas[0]);

      expect(cuentaServiceMock.eliminar).not.toHaveBeenCalled();
    });
  });

  describe('cerrar', () => {
    it('should close the form and reset data', () => {
      component.mostraFormulario = true;
      component.cerrar();

      expect(component.mostraFormulario).toBe(false);
      expect(component.cuentaEditando).toBeNull();
      expect(Object.keys(component.novaCuenta).length).toBe(0);
    });
  });
});
