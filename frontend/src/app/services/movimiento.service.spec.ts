import { describe, it, expect, beforeEach, vi } from 'vitest';
import { MovimientoService, Movimiento } from './movimiento.service';

describe('MovimientoService', () => {
  let service: MovimientoService;
  let httpClientMock: any;

  beforeEach(() => {
    httpClientMock = {
      get: vi.fn(),
      post: vi.fn(),
      delete: vi.fn()
    };

    service = new MovimientoService(httpClientMock);
  });

  describe('obtenerTodos', () => {
    it('should fetch all movimientos', () => {
      const mockMovimientos: Movimiento[] = [
        {
          movimientoId: 1,
          fecha: '2024-01-15',
          tipoMovimiento: 'Depósito',
          valor: 1000,
          saldo: 2500,
          cuentaId: 1
        }
      ];

      httpClientMock.get.mockReturnValue({
        subscribe: (callback: any) => callback(mockMovimientos)
      });

      service.obtenerTodos().subscribe((data) => {
        expect(data).toEqual(mockMovimientos);
        expect(httpClientMock.get).toHaveBeenCalledWith('http://localhost:5001/movimientos');
      });
    });
  });

  describe('obtenerPorId', () => {
    it('should fetch movimiento by id', () => {
      const mockMovimiento: Movimiento = {
        movimientoId: 1,
        fecha: '2024-01-15',
        tipoMovimiento: 'Depósito',
        valor: 1000,
        saldo: 2500,
        cuentaId: 1
      };

      httpClientMock.get.mockReturnValue({
        subscribe: (callback: any) => callback(mockMovimiento)
      });

      service.obtenerPorId(1).subscribe((data) => {
        expect(data).toEqual(mockMovimiento);
      });
    });
  });

  describe('obtenerPorCuentaId', () => {
    it('should fetch movimientos by cuenta id', () => {
      const mockMovimientos: Movimiento[] = [
        {
          movimientoId: 1,
          fecha: '2024-01-15',
          tipoMovimiento: 'Depósito',
          valor: 1000,
          saldo: 2500,
          cuentaId: 1
        }
      ];

      httpClientMock.get.mockReturnValue({
        subscribe: (callback: any) => callback(mockMovimientos)
      });

      service.obtenerPorCuentaId(1).subscribe((data) => {
        expect(data).toEqual(mockMovimientos);
      });
    });
  });

  describe('crear', () => {
    it('should create a new movimiento', () => {
      const newMovimiento: Movimiento = {
        fecha: '2024-01-20',
        tipoMovimiento: 'Retiro',
        valor: -500,
        saldo: 2000,
        cuentaId: 1
      };

      httpClientMock.post.mockReturnValue({
        subscribe: (callback: any) => callback({ ...newMovimiento, movimientoId: 2 })
      });

      service.crear(newMovimiento).subscribe((data) => {
        expect(data.tipoMovimiento).toBe('Retiro');
        expect(httpClientMock.post).toHaveBeenCalled();
      });
    });

    it('should validate required fields before creating', () => {
      const invalidMovimiento: Movimiento = {
        fecha: '',
        tipoMovimiento: '',
        valor: 0,
        saldo: 0,
        cuentaId: 0
      };

      httpClientMock.post.mockReturnValue({
        subscribe: (_: any, errorCallback: any) => {
          errorCallback(new Error('Validation error'));
        }
      });

      service.crear(invalidMovimiento).subscribe(
        () => {},
        (error) => {
          expect(error).toBeDefined();
        }
      );
    });
  });

  describe('eliminar', () => {
    it('should delete a movimiento', () => {
      httpClientMock.delete.mockReturnValue({
        subscribe: (callback: any) => callback(null)
      });

      service.eliminar(1).subscribe(() => {
        expect(httpClientMock.delete).toHaveBeenCalledWith(
          'http://localhost:5001/movimientos/1'
        );
      });
    });
  });
});
