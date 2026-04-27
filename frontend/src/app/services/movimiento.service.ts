import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../config/api.config';

export interface Movimiento {
  movimientoId?: number;
  fecha: string;
  tipoMovimiento: string;
  valor: number;
  saldo: number;
  cuentaId: number;
}

@Injectable({
  providedIn: 'root'
})
export class MovimientoService {
  private apiUrl = `${API_CONFIG.baseUrl}${API_CONFIG.endpoints.movimientos}`;

  constructor(private http: HttpClient) { }

  obtenerTodos(): Observable<Movimiento[]> {
    return this.http.get<Movimiento[]>(this.apiUrl);
  }

  obtenerPorId(id: number): Observable<Movimiento> {
    return this.http.get<Movimiento>(`${this.apiUrl}/${id}`);
  }

  obtenerPorCuentaId(cuentaId: number): Observable<Movimiento[]> {
    return this.http.get<Movimiento[]>(`${this.apiUrl}/cuenta/${cuentaId}`);
  }

  crear(movimiento: Movimiento): Observable<Movimiento> {
    return this.http.post<Movimiento>(this.apiUrl, movimiento);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
