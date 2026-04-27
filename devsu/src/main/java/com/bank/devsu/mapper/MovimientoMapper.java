package com.bank.devsu.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bank.devsu.dto.movimiento.MovimientoRequest;
import com.bank.devsu.dto.movimiento.MovimientoResponse;
import com.bank.devsu.entity.Movimiento;

@Component
public class MovimientoMapper {

    public Movimiento toEntity(MovimientoRequest request) {
        if (request == null) {
			return null;
		}
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setValor(request.getValor());
        return movimiento;
    }

    public MovimientoResponse toResponse(Movimiento movimiento) {
        if (movimiento == null) {
			return null;
		}
        MovimientoResponse response = new MovimientoResponse();
        response.setMovimientoId(movimiento.getMovimientoId());
        response.setFecha(movimiento.getFecha());
        response.setTipoMovimiento(movimiento.getTipoMovimiento());
        response.setValor(movimiento.getValor());
        response.setSaldo(movimiento.getSaldo());
        if (movimiento.getCuenta() != null) {
            response.setCuentaId(movimiento.getCuenta().getCuentaId());
        }
        return response;
    }

    public List<MovimientoResponse> toResponseList(List<Movimiento> movimientos) {
        if (movimientos == null) {
			return null;
		}
        return movimientos.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
