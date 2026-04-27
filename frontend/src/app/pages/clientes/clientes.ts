import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteService, Cliente } from '../../services/cliente.service';
import { ClienteFormComponent } from './cliente-form/cliente-form';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [CommonModule, FormsModule, ClienteFormComponent],
  templateUrl: './clientes.html',
  styleUrl: './clientes.css'
})
export class ClientesComponent implements OnInit {
  clientes: Cliente[] = [];
  clientesFiltrados: Cliente[] = [];
  mostraFormulario = false;
  clienteEditando: Cliente | null = null;
  busqueda = '';
  mensaje = '';
  tipoMensaje: 'exito' | 'error' = 'exito';

  constructor(private clienteService: ClienteService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.cargarClientes();
  }

  cargarClientes() {
    this.clienteService.obtenerTodos().subscribe({
      next: (data) => {
        this.clientes = data;
        this.filtrar();
        this.cdr.detectChanges();
      },
      error: () => this.mostrarError('Error al cargar clientes')
    });
  }

  filtrar() {
    this.clientesFiltrados = !this.busqueda.trim() ? this.clientes :
      this.clientes.filter(c =>
        c.nombre.toLowerCase().includes(this.busqueda.toLowerCase()) ||
        c.identificacion.includes(this.busqueda)
      );
  }

  nuevoCliente() {
    this.clienteEditando = null;
    this.mostraFormulario = true;
  }

  editar(cliente: Cliente) {
    this.clienteEditando = { ...cliente };
    this.mostraFormulario = true;
  }

  eliminar(cliente: Cliente) {
    if (confirm(`¿Eliminar cliente ${cliente.nombre}?`)) {
      this.clienteService.eliminar(cliente.personaId!).subscribe({
        next: () => {
          this.cargarClientes();
          this.mostrarExito('Cliente eliminado');
        },
        error: () => this.mostrarError('Error al eliminar')
      });
    }
  }

  clienteGuardado() {
    this.cargarClientes();
    this.cerrarFormulario();
    this.mostrarExito('Cliente guardado');
  }

  cerrarFormulario() {
    this.mostraFormulario = false;
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
