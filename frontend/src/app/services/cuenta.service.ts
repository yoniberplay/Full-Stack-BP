import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../config/api.config';

export interface Cuenta {
  cuentaId?: number;
  numeroCuenta: string;
  tipoCuenta: string;
  saldoInicial: number;
  saldoDisponible: number;
  estado: boolean;
  personaId: number;
}

@Injectable({
  providedIn: 'root'
})
export class CuentaService {
  private apiUrl = `${API_CONFIG.baseUrl}${API_CONFIG.endpoints.cuentas}`;

  constructor(private http: HttpClient) { }

  obtenerTodas(): Observable<Cuenta[]> {
    return this.http.get<Cuenta[]>(this.apiUrl);
  }

  obtenerPorId(id: number): Observable<Cuenta> {
    return this.http.get<Cuenta>(`${this.apiUrl}/${id}`);
  }

  obtenerPorClienteId(clienteId: number): Observable<Cuenta[]> {
    return this.http.get<Cuenta[]>(`${this.apiUrl}/cliente/${clienteId}`);
  }

  buscar(numeroCuenta: string): Observable<Cuenta[]> {
    return this.http.get<Cuenta[]>(`${this.apiUrl}/buscar?numeroCuenta=${numeroCuenta}`);
  }

  crear(cuenta: Cuenta): Observable<Cuenta> {
    return this.http.post<Cuenta>(this.apiUrl, cuenta);
  }

  actualizar(id: number, cuenta: Cuenta): Observable<Cuenta> {
    return this.http.put<Cuenta>(`${this.apiUrl}/${id}`, cuenta);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
