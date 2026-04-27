import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReporteService, ReporteDTO } from '../../services/reporte.service';
import { ClienteService, Cliente } from '../../services/cliente.service';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reportes.html',
  styleUrl: './reportes.css'
})
export class ReportesComponent implements OnInit {
  clientes: Cliente[] = [];
  reporte: ReporteDTO | null = null;
  personaId: number | null = null;
  fechaInicio = '';
  fechaFin = '';
  mensaje = '';
  tipoMensaje: 'exito' | 'error' = 'exito';
  cargando = false;

  constructor(private reporteService: ReporteService, private clienteService: ClienteService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.cargarClientes();
    const hoy = new Date().toISOString().split('T')[0];
    const hace30 = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
    this.fechaInicio = hace30;
    this.fechaFin = hoy;
  }

  cargarClientes() {
    this.clienteService.obtenerTodos().subscribe({
      next: (data) => {
        this.clientes = data;
        this.cdr.detectChanges();
      },
      error: () => this.mostrarError('Error al cargar clientes')
    });
  }

  generarReporte() {
    if (!this.personaId || !this.fechaInicio || !this.fechaFin) {
      this.mostrarError('Selecciona cliente y fechas');
      return;
    }

    this.cargando = true;
    const fi = `${this.fechaInicio}T00:00:00`;
    const ff = `${this.fechaFin}T23:59:59`;

    this.reporteService.generarReporte(this.personaId, fi, ff).subscribe({
      next: (data) => {
        this.reporte = data;
        this.cargando = false;
        this.mostrarExito('Reporte generado');
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error:', err);
        this.mostrarError('Error al generar reporte');
      }
    });
  }

  descargarPDF() {
    if (!this.reporte) {
      this.mostrarError('Genera el reporte primero');
      return;
    }

    const ventana = window.open('', '_blank');
    if (!ventana) {
      this.mostrarError('Permite popups para descargar el PDF');
      return;
    }

    ventana.document.write(this.buildPdfHtml());
    ventana.document.close();
    ventana.focus();
    setTimeout(() => {
      ventana.print();
      ventana.close();
    }, 500);
  }

  private buildPdfHtml(): string {
    if (!this.reporte) return '';
    const r = this.reporte;

    const cuentasHtml = r.cuentas.map(cuenta => {
      const filas = cuenta.movimientos.map(mov => `
        <tr>
          <td>${new Date(mov.fecha).toLocaleDateString()}</td>
          <td>${mov.tipo}</td>
          <td style="color:${mov.valor < 0 ? '#dc3545' : '#28a745'}">
            $${Math.abs(mov.valor).toFixed(2)}
          </td>
          <td>$${mov.saldo.toFixed(2)}</td>
        </tr>
      `).join('');

      return `
        <div class="cuenta">
          <h3>${cuenta.numeroCuenta} &mdash; ${cuenta.tipoCuenta}</h3>
          <p><strong>Saldo actual:</strong> $${cuenta.saldoActual.toFixed(2)}</p>
          <p><strong>Total débitos:</strong> $${cuenta.totalDebitos.toFixed(2)} &nbsp;
             <strong>Total créditos:</strong> $${cuenta.totalCreditos.toFixed(2)}</p>
          <table>
            <thead>
              <tr><th>Fecha</th><th>Tipo</th><th>Valor</th><th>Saldo</th></tr>
            </thead>
            <tbody>${filas || '<tr><td colspan="4">Sin movimientos</td></tr>'}</tbody>
          </table>
        </div>
      `;
    }).join('');

    return `
      <!DOCTYPE html>
      <html lang="es">
      <head>
        <meta charset="UTF-8">
        <title>Reporte - ${r.clienteNombre}</title>
        <style>
          * { box-sizing: border-box; margin: 0; padding: 0; }
          body { font-family: Arial, sans-serif; font-size: 13px; color: #333; padding: 30px; }
          h1 { font-size: 20px; margin-bottom: 4px; color: #4a4a8a; }
          h2 { font-size: 15px; margin: 20px 0 8px; color: #4a4a8a; border-bottom: 2px solid #4a4a8a; padding-bottom: 4px; }
          h3 { font-size: 13px; margin: 12px 0 6px; color: #555; }
          .header { border-bottom: 3px solid #4a4a8a; padding-bottom: 12px; margin-bottom: 16px; }
          .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 6px; background: #f5f5f5; padding: 12px; border-radius: 4px; margin-bottom: 20px; }
          .info-grid p { margin: 2px 0; }
          .cuenta { margin-bottom: 24px; page-break-inside: avoid; }
          table { width: 100%; border-collapse: collapse; margin-top: 8px; font-size: 12px; }
          th { background: #4a4a8a; color: white; padding: 7px 10px; text-align: left; }
          td { padding: 6px 10px; border-bottom: 1px solid #ddd; }
          tr:nth-child(even) td { background: #f9f9f9; }
          .totales { margin-top: 20px; padding: 12px; background: #eef; border-radius: 4px; font-weight: bold; }
          @media print {
            body { padding: 15px; }
            button { display: none; }
          }
        </style>
      </head>
      <body>
        <div class="header">
          <h1>Reporte de Cuenta</h1>
          <p>Generado el ${new Date().toLocaleDateString()}</p>
        </div>
        <div class="info-grid">
          <p><strong>Cliente:</strong> ${r.clienteNombre}</p>
          <p><strong>Identificación:</strong> ${r.identificacion}</p>
          <p><strong>Periodo:</strong> ${this.fechaInicio} al ${this.fechaFin}</p>
        </div>
        <h2>Detalle de Cuentas</h2>
        ${cuentasHtml}
        <div class="totales">
          <p>Total débitos: $${r.totalDebitos.toFixed(2)} &nbsp;&nbsp;
             Total créditos: $${r.totalCreditos.toFixed(2)}</p>
        </div>
      </body>
      </html>
    `;
  }

  getNombreCliente(): string {
    return this.clientes.find(c => c.personaId === this.personaId)?.nombre || '';
  }

  mostrarExito(msg: string) {
    this.mensaje = msg;
    this.tipoMensaje = 'exito';
    setTimeout(() => this.mensaje = '', 3000);
  }

  mostrarError(msg: string) {
    this.mensaje = msg;
    this.tipoMensaje = 'error';
    setTimeout(() => this.mensaje = '', 3000);
  }
}
