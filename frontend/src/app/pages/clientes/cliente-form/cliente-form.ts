import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteService, Cliente } from '../../../services/cliente.service';

@Component({
  selector: 'app-cliente-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cliente-form.html',
  styleUrl: './cliente-form.css'
})
export class ClienteFormComponent {
  @Input() cliente: Cliente | null = null;
  @Output() guardado = new EventEmitter<void>();
  @Output() cancelado = new EventEmitter<void>();

  formulario: Cliente = {
    nombre: '',
    genero: '',
    edad: 0,
    identificacion: '',
    direccion: '',
    telefono: '',
    contrasena: '',
    estado: true
  };

  touched: { [key: string]: boolean } = {
    nombre: false,
    genero: false,
    edad: false,
    identificacion: false,
    direccion: false,
    telefono: false,
    contrasena: false
  };

  constructor(private clienteService: ClienteService) {}

  ngOnInit() {
    if (this.cliente) {
      this.formulario = { ...this.cliente };
    }
  }

  get isNombreValid(): boolean {
    return !!(this.formulario.nombre && this.formulario.nombre.trim().length >= 3);
  }

  get isEdadValid(): boolean {
    return !!(this.formulario.edad >= 18 && this.formulario.edad <= 120);
  }

  get isIdentificacionValid(): boolean {
    return !!(this.formulario.identificacion && this.formulario.identificacion.trim().length > 0);
  }

  get isTelefonoValid(): boolean {
    return !!(this.formulario.telefono && this.formulario.telefono.replace(/\D/g, '').length >= 10);
  }

  get isPasswordValid(): boolean {
    return !!(this.formulario.contrasena && this.formulario.contrasena.length >= 6);
  }

  get isFormValid(): boolean {
    return !!(
      this.isNombreValid &&
      !!this.formulario.genero &&
      this.isEdadValid &&
      this.isIdentificacionValid &&
      !!this.formulario.direccion &&
      this.isTelefonoValid &&
      this.isPasswordValid
    );
  }

  markAsTouched(field: string) {
    this.touched[field] = true;
  }

  guardar() {
    Object.keys(this.touched).forEach(field => {
      this.touched[field] = true;
    });

    if (!this.isFormValid) {
      return;
    }

    if (this.cliente) {
      this.clienteService.actualizar(this.cliente.personaId!, this.formulario).subscribe({
        next: () => this.guardado.emit(),
        error: (err) => console.error('Error al actualizar:', err)
      });
    } else {
      this.clienteService.crear(this.formulario).subscribe({
        next: () => this.guardado.emit(),
        error: (err) => console.error('Error al crear:', err)
      });
    }
  }

  cancelar() {
    this.cancelado.emit();
  }
}
