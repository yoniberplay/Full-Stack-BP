import { describe, it, expect, beforeEach, vi } from 'vitest';
import { ReporteService, ReporteDTO, CuentaReporte, MovimientoReporte } from './reporte.service';

describe('ReporteService', () => {
  let service: ReporteService;
  let httpClientMock: any;

  beforeEach(() => {
    httpClientMock = {
      get: vi.fn()
    };

    service = new ReporteService(httpClientMock);
  });

  describe('generarReporte', () => {
    it('should generate report with valid parameters', () => {
      const mockReporte: ReporteDTO = {
        clienteNombre: 'Juan Pérez',
        identificacion: '123456',
        fechaInicio: '2024-01-01T00:00:00',
        fechaFin: '2024-01-31T23:59:59',
        cuentas: [
          {
            numeroCuenta: '1234567890',
            tipoCuenta: 'Ahorros',
            saldoActual: 2500,
            totalDebitos: 1000,
            totalCreditos: 3500,
            movimientos: [
              {
                fecha: '2024-01-15',
                tipo: 'Depósito',
                valor: 1000,
                saldo: 2500
              }
            ]
          }
        ],
        totalDebitos: 1000,
        totalCreditos: 3500
      };

      httpClientMock.get.mockReturnValue({
        subscribe: (callback: any) => callback(mockReporte)
      });

      service.generarReporte(1, '2024-01-01T00:00:00', '2024-01-31T23:59:59').subscribe((data) => {
        expect(data).toEqual(mockReporte);
        expect(data.clienteNombre).toBe('Juan Pérez');
        expect(data.cuentas.length).toBeGreaterThan(0);
      });
    });

    it('should call the correct API endpoint', () => {
      httpClientMock.get.mockReturnValue({
        subscribe: (callback: any) => callback({})
      });

      service.generarReporte(1, '2024-01-01T00:00:00', '2024-01-31T23:59:59').subscribe(() => {
        const expectedUrl = 'http://localhost:5001/reportes?personaId=1&fechaInicio=2024-01-01T00:00:00&fechaFin=2024-01-31T23:59:59';
        expect(httpClientMock.get).toHaveBeenCalledWith(expectedUrl);
      });
    });

    it('should handle empty accounts in report', () => {
      const mockReporte: ReporteDTO = {
        clienteNombre: 'Jane Doe',
        identificacion: '654321',
        fechaInicio: '2024-01-01T00:00:00',
        fechaFin: '2024-01-31T23:59:59',
        cuentas: [],
        totalDebitos: 0,
        totalCreditos: 0
      };

      httpClientMock.get.mockReturnValue({
        subscribe: (callback: any) => callback(mockReporte)
      });

      service.generarReporte(2, '2024-01-01T00:00:00', '2024-01-31T23:59:59').subscribe((data) => {
        expect(data.cuentas.length).toBe(0);
        expect(data.totalDebitos).toBe(0);
      });
    });
  });

  describe('descargarPDF', () => {
    it('should download PDF with valid parameters', () => {
      const mockBlob = new Blob(['test'], { type: 'application/pdf' });

      httpClientMock.get.mockReturnValue({
        subscribe: (callback: any) => callback(mockBlob)
      });

      service.descargarPDF(1, '2024-01-01T00:00:00', '2024-01-31T23:59:59').subscribe((data) => {
        expect(data).toBeInstanceOf(Blob);
        expect(data.type).toBe('application/pdf');
      });
    });

    it('should call the correct PDF endpoint', () => {
      const mockBlob = new Blob(['test'], { type: 'application/pdf' });

      httpClientMock.get.mockReturnValue({
        subscribe: (callback: any) => callback(mockBlob)
      });

      service.descargarPDF(1, '2024-01-01T00:00:00', '2024-01-31T23:59:59').subscribe(() => {
        const expectedUrl = 'http://localhost:5001/reportes/pdf?personaId=1&fechaInicio=2024-01-01T00:00:00&fechaFin=2024-01-31T23:59:59';
        expect(httpClientMock.get).toHaveBeenCalledWith(
          expectedUrl,
          { responseType: 'blob' }
        );
      });
    });

    it('should handle PDF download errors gracefully', () => {
      const error = new Error('PDF generation failed');

      httpClientMock.get.mockReturnValue({
        subscribe: (_: any, errorCallback: any) => errorCallback(error)
      });

      service.descargarPDF(1, '2024-01-01T00:00:00', '2024-01-31T23:59:59').subscribe(
        () => {},
        (err) => {
          expect(err).toBeDefined();
        }
      );
    });
  });

  describe('ReporteDTO structure', () => {
    it('should have all required fields', () => {
      const reporte: ReporteDTO = {
        clienteNombre: 'Test',
        identificacion: '123',
        fechaInicio: '2024-01-01',
        fechaFin: '2024-01-31',
        cuentas: [],
        totalDebitos: 0,
        totalCreditos: 0
      };

      expect(reporte.clienteNombre).toBeDefined();
      expect(reporte.identificacion).toBeDefined();
      expect(reporte.fechaInicio).toBeDefined();
      expect(reporte.fechaFin).toBeDefined();
      expect(reporte.cuentas).toBeDefined();
      expect(reporte.totalDebitos).toBeDefined();
      expect(reporte.totalCreditos).toBeDefined();
    });
  });
});
