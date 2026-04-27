import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MovimientoService, Movimiento } from '../../services/movimiento.service';
import { CuentaService, Cuenta } from '../../services/cuenta.service';

@Component({
  selector: 'app-movimientos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './movimientos.html',
  styleUrl: './movimientos.css'
})
export class MovimientosComponent implements OnInit {
  movimientos: Movimiento[] = [];
  movimientosFiltrados: Movimiento[] = [];
  cuentas: Cuenta[] = [];
  mostraFormulario = false;
  mensaje = '';
  tipoMensaje: 'exito' | 'error' = 'exito';
  busqueda = '';

  nuevoMovimiento: Partial<Movimiento> = {};

  constructor(private movimientoService: MovimientoService, private cuentaService: CuentaService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.cargarCuentas();
    this.cargarMovimientos();
  }

  cargarCuentas() {
    this.cuentaService.obtenerTodas().subscribe({
      next: (data) => {
        this.cuentas = data;
        this.cdr.detectChanges();
      },
      error: () => this.mostrarError('Error al cargar cuentas')
    });
  }

  cargarMovimientos() {
    this.movimientoService.obtenerTodos().subscribe({
      next: (data) => {
        this.movimientos = data.sort((a, b) =>
          new Date(b.fecha).getTime() - new Date(a.fecha).getTime());
        this.filtrar();
        this.cdr.detectChanges();
      },
      error: () => this.mostrarError('Error al cargar movimientos')
    });
  }

  filtrar() {
    if (!this.busqueda.trim()) {
      this.movimientosFiltrados = this.movimientos;
    } else {
      const searchTerm = this.busqueda.toLowerCase();
      this.movimientosFiltrados = this.movimientos.filter(m => {
        const numeroCuenta = this.getNombreCuenta(m.cuentaId);
        return (
          numeroCuenta.toLowerCase().includes(searchTerm) ||
          m.tipoMovimiento.toLowerCase().includes(searchTerm) ||
          m.valor.toString().includes(searchTerm)
        );
      });
    }
  }

  nuevoRegistro() {
    this.nuevoMovimiento = {};
    this.mostraFormulario = true;
  }

  guardar() {
    if (!this.nuevoMovimiento.cuentaId || !this.nuevoMovimiento.tipoMovimiento || !this.nuevoMovimiento.valor) {
      this.mostrarError('Completa todos los campos');
      return;
    }

    this.movimientoService.crear(this.nuevoMovimiento as Movimiento).subscribe({
      next: () => { this.cargarMovimientos(); this.cerrar(); this.mostrarExito('Movimiento registrado'); },
      error: (err) => this.mostrarError(err.error || 'Error')
    });
  }

  eliminar(mov: Movimiento) {
    if (confirm('¿Eliminar movimiento?')) {
      this.movimientoService.eliminar(mov.movimientoId!).subscribe({
        next: () => { this.cargarMovimientos(); this.mostrarExito('Eliminado'); },
        error: () => this.mostrarError('Error')
      });
    }
  }

  cerrar() {
    this.mostraFormulario = false;
    this.nuevoMovimiento = {};
  }

  getNombreCuenta(cuentaId: number): string {
    return this.cuentas.find(c => c.cuentaId === cuentaId)?.numeroCuenta || 'N/A';
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
