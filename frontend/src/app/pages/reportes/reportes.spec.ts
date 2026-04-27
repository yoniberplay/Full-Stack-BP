import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest';
import { ReportesComponent } from './reportes';
import { ReporteService, ReporteDTO } from '../../services/reporte.service';
import { ClienteService, Cliente } from '../../services/cliente.service';

describe('ReportesComponent', () => {
  let component: ReportesComponent;
  let reporteServiceMock: any;
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
    }
  ];

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

  beforeEach(() => {
    const cdrMock = { detectChanges: vi.fn() };

    reporteServiceMock = {
      generarReporte: vi.fn(() => ({
        subscribe: ({ next }: any) => next(mockReporte)
      }))
    };

    clienteServiceMock = {
      obtenerTodos: vi.fn(() => ({
        subscribe: ({ next }: any) => next(mockClientes)
      }))
    };

    component = new ReportesComponent(reporteServiceMock, clienteServiceMock, cdrMock as any);
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  describe('ngOnInit', () => {
    it('should load clientes on initialization', () => {
      component.ngOnInit();

      expect(clienteServiceMock.obtenerTodos).toHaveBeenCalled();
      expect(component.clientes.length).toBe(1);
    });

    it('should set default dates', () => {
      component.ngOnInit();

      expect(component.fechaInicio).toBeDefined();
      expect(component.fechaFin).toBeDefined();
      expect(component.fechaInicio).toBeTruthy();
      expect(component.fechaFin).toBeTruthy();
    });
  });

  describe('cargarClientes', () => {
    it('should fetch and set clientes', () => {
      component.cargarClientes();

      expect(clienteServiceMock.obtenerTodos).toHaveBeenCalled();
      expect(component.clientes).toEqual(mockClientes);
    });
  });

  describe('generarReporte', () => {
    it('should show error when cliente is not selected', () => {
      component.personaId = null;
      component.generarReporte();

      expect(component.mensaje).toBe('Selecciona cliente y fechas');
      expect(component.tipoMensaje).toBe('error');
    });

    it('should show error when dates are not selected', () => {
      component.personaId = 1;
      component.fechaInicio = '';
      component.fechaFin = '';
      component.generarReporte();

      expect(component.mensaje).toBe('Selecciona cliente y fechas');
    });

    it('should generate report with valid parameters', () => {
      component.personaId = 1;
      component.fechaInicio = '2024-01-01';
      component.fechaFin = '2024-01-31';

      component.generarReporte();

      expect(reporteServiceMock.generarReporte).toHaveBeenCalled();
      expect(component.reporte).toEqual(mockReporte);
    });

    it('should set cargando to false after report generation completes', () => {
      component.personaId = 1;
      component.fechaInicio = '2024-01-01';
      component.fechaFin = '2024-01-31';

      component.generarReporte();

      expect(component.cargando).toBe(false);
    });
  });

  describe('descargarPDF', () => {
    it('should show error when no report has been generated', () => {
      component.reporte = null;
      component.descargarPDF();

      expect(component.mensaje).toBe('Genera el reporte primero');
      expect(component.tipoMensaje).toBe('error');
    });

    it('should open print window when report exists', () => {
      component.reporte = mockReporte;
      component.fechaInicio = '2024-01-01';
      component.fechaFin = '2024-01-31';

      const mockWindow = {
        document: { write: vi.fn(), close: vi.fn() },
        focus: vi.fn(),
        print: vi.fn(),
        close: vi.fn()
      };
      vi.spyOn(window, 'open').mockReturnValue(mockWindow as any);

      component.descargarPDF();

      expect(window.open).toHaveBeenCalledWith('', '_blank');
      expect(mockWindow.document.write).toHaveBeenCalled();
    });

    it('should show error when popup is blocked', () => {
      component.reporte = mockReporte;
      vi.spyOn(window, 'open').mockReturnValue(null);

      component.descargarPDF();

      expect(component.mensaje).toBe('Permite popups para descargar el PDF');
      expect(component.tipoMensaje).toBe('error');
    });
  });

  describe('getNombreCliente', () => {
    beforeEach(() => {
      component.clientes = mockClientes;
    });

    it('should return client name for valid cliente id', () => {
      component.personaId = 1;
      const nombre = component.getNombreCliente();

      expect(nombre).toBe('Juan Pérez');
    });

    it('should return empty string for invalid cliente id', () => {
      component.personaId = 999;
      const nombre = component.getNombreCliente();

      expect(nombre).toBe('');
    });
  });

  describe('mostrarExito', () => {
    it('should set success message and clear after timeout', () => {
      vi.useFakeTimers();
      component.mostrarExito('Reporte generado');

      expect(component.mensaje).toBe('Reporte generado');
      expect(component.tipoMensaje).toBe('exito');

      vi.advanceTimersByTime(3000);
      expect(component.mensaje).toBe('');
    });
  });

  describe('mostrarError', () => {
    it('should set error message and clear after timeout', () => {
      vi.useFakeTimers();
      component.mostrarError('Error al generar');

      expect(component.mensaje).toBe('Error al generar');
      expect(component.tipoMensaje).toBe('error');

      vi.advanceTimersByTime(3000);
      expect(component.mensaje).toBe('');
    });
  });
});
