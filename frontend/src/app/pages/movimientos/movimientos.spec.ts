import { describe, it, expect, beforeEach, vi } from 'vitest';
import { MovimientosComponent } from './movimientos';
import { MovimientoService, Movimiento } from '../../services/movimiento.service';
import { CuentaService, Cuenta } from '../../services/cuenta.service';

describe('MovimientosComponent', () => {
  let component: MovimientosComponent;
  let movimientoServiceMock: any;
  let cuentaServiceMock: any;

  const mockMovimientos: Movimiento[] = [
    {
      movimientoId: 1,
      fecha: '2024-01-15',
      tipoMovimiento: 'Depósito',
      valor: 1000,
      saldo: 2500,
      cuentaId: 1
    },
    {
      movimientoId: 2,
      fecha: '2024-01-20',
      tipoMovimiento: 'Retiro',
      valor: -500,
      saldo: 2000,
      cuentaId: 1
    }
  ];

  const mockCuentas: Cuenta[] = [
    {
      cuentaId: 1,
      numeroCuenta: '1234567890',
      tipoCuenta: 'Ahorros',
      saldoInicial: 1000,
      saldoDisponible: 2000,
      estado: true,
      personaId: 1
    }
  ];

  beforeEach(() => {
    const cdrMock = { detectChanges: vi.fn() };

    movimientoServiceMock = {
      obtenerTodos: vi.fn(() => ({
        subscribe: ({ next }: any) => next(mockMovimientos.map(m => ({ ...m })))
      })),
      crear: vi.fn(() => ({
        subscribe: ({ next }: any) => next({})
      })),
      eliminar: vi.fn(() => ({
        subscribe: ({ next }: any) => next(null)
      }))
    };

    cuentaServiceMock = {
      obtenerTodas: vi.fn(() => ({
        subscribe: ({ next }: any) => next(mockCuentas)
      }))
    };

    component = new MovimientosComponent(movimientoServiceMock, cuentaServiceMock, cdrMock as any);
  });

  describe('ngOnInit', () => {
    it('should load cuentas and movimientos on initialization', () => {
      component.ngOnInit();

      expect(cuentaServiceMock.obtenerTodas).toHaveBeenCalled();
      expect(movimientoServiceMock.obtenerTodos).toHaveBeenCalled();
      expect(component.cuentas.length).toBe(1);
      expect(component.movimientos.length).toBe(2);
    });

    it('should sort movimientos by date in descending order', () => {
      component.ngOnInit();

      expect(component.movimientos[0].fecha).toBe('2024-01-20');
      expect(component.movimientos[1].fecha).toBe('2024-01-15');
    });
  });

  describe('cargarMovimientos', () => {
    it('should fetch and set movimientos', () => {
      component.cargarMovimientos();

      expect(movimientoServiceMock.obtenerTodos).toHaveBeenCalled();
      expect(component.movimientos.length).toBe(2);
    });

    it('should filter movimientos after loading', () => {
      component.cargarMovimientos();

      expect(component.movimientosFiltrados.length).toBe(2);
    });
  });

  describe('filtrar', () => {
    beforeEach(() => {
      component.movimientos = mockMovimientos;
      component.cuentas = mockCuentas;
    });

    it('should return all movimientos when search is empty', () => {
      component.busqueda = '';
      component.filtrar();

      expect(component.movimientosFiltrados.length).toBe(2);
    });

    it('should filter by numeroCuenta', () => {
      component.busqueda = '1234567890';
      component.filtrar();

      expect(component.movimientosFiltrados.length).toBe(2);
    });

    it('should filter by tipoMovimiento', () => {
      component.busqueda = 'depósito';
      component.filtrar();

      expect(component.movimientosFiltrados.length).toBe(1);
      expect(component.movimientosFiltrados[0].tipoMovimiento).toBe('Depósito');
    });

    it('should filter by valor', () => {
      component.busqueda = '1000';
      component.filtrar();

      expect(component.movimientosFiltrados.length).toBeGreaterThan(0);
    });

    it('should be case insensitive', () => {
      component.busqueda = 'RETIRO';
      component.filtrar();

      expect(component.movimientosFiltrados.length).toBe(1);
    });
  });

  describe('nuevoRegistro', () => {
    it('should open form for new movimiento', () => {
      component.nuevoRegistro();

      expect(component.mostraFormulario).toBe(true);
      expect(Object.keys(component.nuevoMovimiento).length).toBe(0);
    });
  });

  describe('guardar', () => {
    it('should show error when required fields are missing', () => {
      component.nuevoMovimiento = {};
      component.guardar();

      expect(component.mensaje).toBe('Completa todos los campos');
      expect(component.tipoMensaje).toBe('error');
    });

    it('should create movimiento when valid', () => {
      component.nuevoMovimiento = {
        cuentaId: 1,
        tipoMovimiento: 'Depósito',
        valor: 500
      };

      component.guardar();

      expect(movimientoServiceMock.crear).toHaveBeenCalled();
    });
  });

  describe('eliminar', () => {
    it('should delete movimiento when confirmed', () => {
      vi.spyOn(window, 'confirm').mockReturnValue(true);

      component.eliminar(mockMovimientos[0]);

      expect(movimientoServiceMock.eliminar).toHaveBeenCalledWith(1);
    });

    it('should not delete when not confirmed', () => {
      vi.spyOn(window, 'confirm').mockReturnValue(false);

      component.eliminar(mockMovimientos[0]);

      expect(movimientoServiceMock.eliminar).not.toHaveBeenCalled();
    });
  });

  describe('getNombreCuenta', () => {
    beforeEach(() => {
      component.cuentas = mockCuentas;
    });

    it('should return account number for valid cuenta id', () => {
      const nombre = component.getNombreCuenta(1);

      expect(nombre).toBe('1234567890');
    });

    it('should return N/A for invalid cuenta id', () => {
      const nombre = component.getNombreCuenta(999);

      expect(nombre).toBe('N/A');
    });
  });

  describe('cerrar', () => {
    it('should close the form and reset data', () => {
      component.mostraFormulario = true;
      component.cerrar();

      expect(component.mostraFormulario).toBe(false);
      expect(Object.keys(component.nuevoMovimiento).length).toBe(0);
    });
  });
});
