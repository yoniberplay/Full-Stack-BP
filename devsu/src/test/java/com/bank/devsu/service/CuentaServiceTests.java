package com.bank.devsu.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bank.devsu.config.CuentaConfigProperties;
import com.bank.devsu.entity.Cliente;
import com.bank.devsu.entity.Cuenta;
import com.bank.devsu.enums.TipoCuenta;
import com.bank.devsu.repository.ClienteRepository;
import com.bank.devsu.repository.CuentaRepository;

public class CuentaServiceTests {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CuentaConfigProperties cuentaConfig;

    @InjectMocks
    private CuentaService cuentaService;

    private Cliente cliente;
    private Cuenta cuenta;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(cuentaConfig.getLimiteDiarioRetiro()).thenReturn(new BigDecimal("1000"));

        cliente = new Cliente("Jose Lema", "M", 30, "1234567890", "Otavalo", "098254785", "1234", true);
        cliente.setPersonaId(1L);

        cuenta = new Cuenta("478758", TipoCuenta.AHORROS, new BigDecimal("2000"), true, cliente);
        cuenta.setCuentaId(1L);
        cuenta.setLimiteDiarioRetiro(new BigDecimal("1000"));
    }

    @Test
    public void testValidarSaldoDisponible_SaldoSuficiente() {
        BigDecimal monto = new BigDecimal("500");
        assertDoesNotThrow(() -> cuentaService.validarSaldoDisponible(cuenta, monto));
    }

    @Test
    public void testValidarSaldoDisponible_SaldoInsuficiente() {
        BigDecimal monto = new BigDecimal("3000");
        assertThrows(IllegalArgumentException.class, () -> cuentaService.validarSaldoDisponible(cuenta, monto));
    }

    @Test
    public void testValidarLimiteDiarioRetiro_DentroDelLimite() {
        BigDecimal monto = new BigDecimal("500");
        cuenta.setTotalRetirosDia(BigDecimal.ZERO);
        assertDoesNotThrow(() -> cuentaService.validarLimiteDiarioRetiro(cuenta, monto));
    }

    @Test
    public void testValidarLimiteDiarioRetiro_ExcedeLimite() {
        BigDecimal monto = new BigDecimal("600");
        cuenta.setTotalRetirosDia(new BigDecimal("500"));
        assertThrows(IllegalArgumentException.class, () -> cuentaService.validarLimiteDiarioRetiro(cuenta, monto));
    }

    @Test
    public void testCrearCuenta_Exitosa() {
        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.empty());
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

        Cuenta cuentaCreada = cuentaService.crear(cuenta, 1L);

        assertNotNull(cuentaCreada);
        assertEquals("478758", cuentaCreada.getNumeroCuenta());
        verify(cuentaRepository, times(1)).save(any(Cuenta.class));
    }

    @Test
    public void testCrearCuenta_NumeroDuplicado() {
        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(cuenta));

        assertThrows(IllegalArgumentException.class, () -> cuentaService.crear(cuenta, 1L));
    }

    @Test
    public void testActualizarSaldoYRetiros_Debito() {
        BigDecimal montoAntes = cuenta.getSaldoDisponible();
        BigDecimal monto = new BigDecimal("500");

        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

        cuentaService.actualizarSaldoYRetiros(cuenta, monto, "Débito");

        BigDecimal montoEsperado = montoAntes.subtract(monto);
        assertEquals(montoEsperado, cuenta.getSaldoDisponible());
    }

    @Test
    public void testActualizarSaldoYRetiros_Credito() {
        BigDecimal montoAntes = cuenta.getSaldoDisponible();
        BigDecimal monto = new BigDecimal("500");

        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

        cuentaService.actualizarSaldoYRetiros(cuenta, monto, "Crédito");

        BigDecimal montoEsperado = montoAntes.add(monto);
        assertEquals(montoEsperado, cuenta.getSaldoDisponible());
    }
}
