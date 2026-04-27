import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../config/api.config';

export interface ReporteDTO {
  clienteNombre: string;
  identificacion: string;
  fechaInicio: string;
  fechaFin: string;
  cuentas: CuentaReporte[];
  totalDebitos: number;
  totalCreditos: number;
}

export interface CuentaReporte {
  numeroCuenta: string;
  tipoCuenta: string;
  saldoActual: number;
  totalDebitos: number;
  totalCreditos: number;
  movimientos: MovimientoReporte[];
}

export interface MovimientoReporte {
  fecha: string;
  tipo: string;
  valor: number;
  saldo: number;
}

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private apiUrl = `${API_CONFIG.baseUrl}${API_CONFIG.endpoints.reportes}`;

  constructor(private http: HttpClient) { }

  generarReporte(personaId: number, fechaInicio: string, fechaFin: string): Observable<ReporteDTO> {
    return this.http.get<ReporteDTO>(`${this.apiUrl}?personaId=${personaId}&fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`);
  }

  descargarPDF(personaId: number, fechaInicio: string, fechaFin: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/pdf?personaId=${personaId}&fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`, { responseType: 'blob' });
  }
}
