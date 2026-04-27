package com.bank.devsu.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bank.devsu.dto.cuenta.CuentaRequest;
import com.bank.devsu.dto.cuenta.CuentaResponse;
import com.bank.devsu.entity.Cuenta;

@Component
public class CuentaMapper {

    public Cuenta toEntity(CuentaRequest request) {
        if (request == null) {
			return null;
		}
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(request.getNumeroCuenta());
        cuenta.setTipoCuenta(request.getTipoCuenta());
        cuenta.setSaldoInicial(request.getSaldoInicial());
        cuenta.setEstado(request.getEstado());
        return cuenta;
    }

    public CuentaResponse toResponse(Cuenta cuenta) {
        if (cuenta == null) {
			return null;
		}
        CuentaResponse response = new CuentaResponse();
        response.setCuentaId(cuenta.getCuentaId());
        response.setNumeroCuenta(cuenta.getNumeroCuenta());
        response.setTipoCuenta(cuenta.getTipoCuenta());
        response.setSaldoInicial(cuenta.getSaldoInicial());
        response.setSaldoDisponible(cuenta.getSaldoDisponible());
        response.setEstado(cuenta.getEstado());
        response.setLimiteDiarioRetiro(cuenta.getLimiteDiarioRetiro());
        return response;
    }

    public List<CuentaResponse> toResponseList(List<Cuenta> cuentas) {
        if (cuentas == null) {
			return null;
		}
        return cuentas.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
