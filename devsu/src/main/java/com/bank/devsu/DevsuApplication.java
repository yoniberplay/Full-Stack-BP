package com.bank.devsu;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.bank.devsu.entity.Cliente;
import com.bank.devsu.entity.Cuenta;
import com.bank.devsu.entity.Movimiento;
import com.bank.devsu.enums.TipoCuenta;
import com.bank.devsu.repository.ClienteRepository;
import com.bank.devsu.repository.CuentaRepository;
import com.bank.devsu.repository.MovimientoRepository;


@SpringBootApplication
@ComponentScan(basePackages = "com.bank.devsu")
public class DevsuApplication implements CommandLineRunner {

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private CuentaRepository cuentaRepository;

	@Autowired
	private MovimientoRepository movimientoRepository;

	public static void main(String[] args) {
		SpringApplication.run(DevsuApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			System.out.println("Corriiendo en http://localhost:5001");
			//cargarDatosIniciales();
			System.out.println("Datos cargados.");
		} catch (Exception e) {
			System.out.println("Error cargando"+e.getMessage());
		}

	}
	
	private void cargarDatosIniciales() {
		// Crear clientes
		Cliente cliente1 = new Cliente("Jose Lema", "M", 45, "0950576829", "Otavalo sn y principal", "098254785", "1234", true);
		Cliente cliente2 = new Cliente("Marianela Montalvo", "F", 38, "1234567890", "Amazonas y NNUU", "097548965", "5678", true);
		Cliente cliente3 = new Cliente("Juan Osorio", "M", 35, "0987654321", "13 junio y Equinoccial", "098874587", "1245", true);

		cliente1 = clienteRepository.save(cliente1);
		cliente2 = clienteRepository.save(cliente2);
		cliente3 = clienteRepository.save(cliente3);

		// Crear cuentas
		Cuenta cuenta1 = new Cuenta("478758", TipoCuenta.AHORROS ,new BigDecimal("2000"), true, cliente1);
		Cuenta cuenta2 = new Cuenta("225487",TipoCuenta.CORRIENTE, new BigDecimal("100"), true, cliente2);
		Cuenta cuenta3 = new Cuenta("495878", TipoCuenta.AHORROS, new BigDecimal("0"), true, cliente3);
		Cuenta cuenta4 = new Cuenta("496825",TipoCuenta.CORRIENTE, new BigDecimal("540"), true, cliente2);

		cuenta1 = cuentaRepository.save(cuenta1);
		cuenta2 = cuentaRepository.save(cuenta2);
		cuenta3 = cuentaRepository.save(cuenta3);
		cuenta4 = cuentaRepository.save(cuenta4);

		// Crear movimientos de ejemplo
		Movimiento mov1 = new Movimiento(LocalDateTime.now().minusDays(5), "Retiro", new BigDecimal("-575"), new BigDecimal("1425"), cuenta1);
		Movimiento mov2 = new Movimiento(LocalDateTime.now().minusDays(4), "Depósito", new BigDecimal("600"), new BigDecimal("700"), cuenta2);
		Movimiento mov3 = new Movimiento(LocalDateTime.now().minusDays(3), "Depósito", new BigDecimal("150"), new BigDecimal("150"), cuenta3);
		Movimiento mov4 = new Movimiento(LocalDateTime.now().minusDays(2), "Retiro", new BigDecimal("-540"), new BigDecimal("0"), cuenta4);

		movimientoRepository.save(mov1);
		movimientoRepository.save(mov2);
		movimientoRepository.save(mov3);
		movimientoRepository.save(mov4);

		// Actualizar saldos
		cuenta1.setSaldoDisponible(new BigDecimal("1425"));
		cuenta2.setSaldoDisponible(new BigDecimal("700"));
		cuenta3.setSaldoDisponible(new BigDecimal("150"));
		cuenta4.setSaldoDisponible(BigDecimal.ZERO);

		cuentaRepository.save(cuenta1);
		cuentaRepository.save(cuenta2);
		cuentaRepository.save(cuenta3);
		cuentaRepository.save(cuenta4);
	}


}

