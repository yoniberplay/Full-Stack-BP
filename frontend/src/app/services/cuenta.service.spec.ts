import { describe, it, expect, beforeEach, vi } from 'vitest';
import { CuentaService, Cuenta } from './cuenta.service';

describe('CuentaService', () => {
  let service: CuentaService;
  let httpClientMock: any;

  beforeEach(() => {
    httpClientMock = {
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      delete: vi.fn()
    };

    service = new CuentaService(httpClientMock);
  });

  describe('obtenerTodas', () => {
    it('should fetch all cuentas', () => {
      const mockCuentas: Cuenta[] = [
        {
          cuentaId: 1,
          numeroCuenta: '1234567890',
          tipoCuenta: 'Ahorros',
          saldoInicial: 1000,
          saldoDisponible: 1500,
          estado: true,
          personaId: 1
        }
      ];

      httpClientMock.get.mockReturnValue({
        subscribe: (callback: any) => callback(mockCuentas)
      });

      service.obtenerTodas().subscribe((data) => {
        expect(data).toEqual(mockCuentas);
        expect(httpClientMock.get).toHaveBeenCalledWith('http://localhost:5001/cuentas');
      });
    });
  });

  describe('obtenerPorId', () => {
    it('should fetch cuenta by id', () => {
      const mockCuenta: Cuenta = {
        cuentaId: 1,
        numeroCuenta: '1234567890',
        tipoCuenta: 'Ahorros',
        saldoInicial: 1000,
        saldoDisponible: 1500,
        estado: true,
        personaId: 1
      };

      httpClientMock.get.mockReturnValue({
        subscribe: (callback: any) => callback(mockCuenta)
      });

      service.obtenerPorId(1).subscribe((data) => {
        expect(data).toEqual(mockCuenta);
      });
    });
  });

  describe('obtenerPorClienteId', () => {
    it('should fetch cuentas by cliente id', () => {
      const mockCuentas: Cuenta[] = [
        {
          cuentaId: 1,
          numeroCuenta: '1234567890',
          tipoCuenta: 'Ahorros',
          saldoInicial: 1000,
          saldoDisponible: 1500,
          estado: true,
          personaId: 1
        }
      ];

      httpClientMock.get.mockReturnValue({
        subscribe: (callback: any) => callback(mockCuentas)
      });

      service.obtenerPorClienteId(1).subscribe((data) => {
        expect(data).toEqual(mockCuentas);
      });
    });
  });

  describe('buscar', () => {
    it('should search cuentas by account number', () => {
      const mockCuentas: Cuenta[] = [
        {
          cuentaId: 1,
          numeroCuenta: '1234567890',
          tipoCuenta: 'Ahorros',
          saldoInicial: 1000,
          saldoDisponible: 1500,
          estado: true,
          personaId: 1
        }
      ];

      httpClientMock.get.mockReturnValue({
        subscribe: (callback: any) => callback(mockCuentas)
      });

      service.buscar('1234567890').subscribe((data) => {
        expect(data).toEqual(mockCuentas);
      });
    });
  });

  describe('crear', () => {
    it('should create a new cuenta', () => {
      const newCuenta: Cuenta = {
        numeroCuenta: '9876543210',
        tipoCuenta: 'Corriente',
        saldoInicial: 500,
        saldoDisponible: 500,
        estado: true,
        personaId: 2
      };

      httpClientMock.post.mockReturnValue({
        subscribe: (callback: any) => callback({ ...newCuenta, cuentaId: 2 })
      });

      service.crear(newCuenta).subscribe((data) => {
        expect(data.numeroCuenta).toBe(newCuenta.numeroCuenta);
        expect(httpClientMock.post).toHaveBeenCalled();
      });
    });
  });

  describe('actualizar', () => {
    it('should update an existing cuenta', () => {
      const updatedCuenta: Cuenta = {
        cuentaId: 1,
        numeroCuenta: '1234567890',
        tipoCuenta: 'Corriente',
        saldoInicial: 1000,
        saldoDisponible: 2000,
        estado: true,
        personaId: 1
      };

      httpClientMock.put.mockReturnValue({
        subscribe: (callback: any) => callback(updatedCuenta)
      });

      service.actualizar(1, updatedCuenta).subscribe((data) => {
        expect(data.tipoCuenta).toBe('Corriente');
        expect(httpClientMock.put).toHaveBeenCalled();
      });
    });
  });

  describe('eliminar', () => {
    it('should delete a cuenta', () => {
      httpClientMock.delete.mockReturnValue({
        subscribe: (callback: any) => callback(null)
      });

      service.eliminar(1).subscribe(() => {
        expect(httpClientMock.delete).toHaveBeenCalledWith(
          'http://localhost:5001/cuentas/1'
        );
      });
    });
  });
});
