import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CuentaService, Cuenta } from '../../services/cuenta.service';
import { ClienteService, Cliente } from '../../services/cliente.service';

@Component({
  selector: 'app-cuentas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cuentas.html',
  styleUrl: './cuentas.css'
})
export class CuentasComponent implements OnInit {
  cuentas: Cuenta[] = [];
  cuentasFiltradas: Cuenta[] = [];
  clientes: Cliente[] = [];
  mostraFormulario = false;
  cuentaEditando: Cuenta | null = null;
  busqueda = '';
  mensaje = '';
  tipoMensaje: 'exito' | 'error' = 'exito';

  novaCuenta: Partial<Cuenta> = {};

  constructor(private cuentaService: CuentaService, private clienteService: ClienteService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.cargarClientes();
    this.cargarCuentas();
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

  cargarCuentas() {
    this.cuentaService.obtenerTodas().subscribe({
      next: (data) => {
        this.cuentas = data;
        this.filtrar();
        this.cdr.detectChanges();
      },
      error: () => this.mostrarError('Error al cargar cuentas')
    });
  }

  filtrar() {
    if (!this.busqueda.trim()) {
      this.cuentasFiltradas = this.cuentas;
    } else {
      const searchTerm = this.busqueda.toLowerCase();
      this.cuentasFiltradas = this.cuentas.filter(c =>
        c.numeroCuenta.toLowerCase().includes(searchTerm) ||
        c.tipoCuenta.toLowerCase().includes(searchTerm)
      );
    }
  }

  nuevoRegistro() {
    this.novaCuenta = { estado: true };
    this.mostraFormulario = true;
  }

  editar(cuenta: Cuenta) {
    this.cuentaEditando = { ...cuenta };
    this.novaCuenta = { ...cuenta };
    this.mostraFormulario = true;
  }

  guardar() {
    if (!this.novaCuenta.numeroCuenta || !this.novaCuenta.tipoCuenta || !this.novaCuenta.personaId) {
      this.mostrarError('Completa todos los campos');
      return;
    }

    if (this.cuentaEditando) {
      this.cuentaService.actualizar(this.cuentaEditando.cuentaId!, this.novaCuenta as Cuenta).subscribe({
        next: () => { this.cargarCuentas(); this.cerrar(); this.mostrarExito('Cuenta actualizada'); },
        error: () => this.mostrarError('Error al actualizar')
      });
    } else {
      this.cuentaService.crear(this.novaCuenta as Cuenta).subscribe({
        next: () => { this.cargarCuentas(); this.cerrar(); this.mostrarExito('Cuenta creada'); },
        error: () => this.mostrarError('Error al crear')
      });
    }
  }

  eliminar(cuenta: Cuenta) {
    if (confirm('¿Eliminar?')) {
      this.cuentaService.eliminar(cuenta.cuentaId!).subscribe({
        next: () => { this.cargarCuentas(); this.mostrarExito('Eliminada'); },
        error: () => this.mostrarError('Error')
      });
    }
  }

  cerrar() {
    this.mostraFormulario = false;
    this.cuentaEditando = null;
    this.novaCuenta = {};
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
