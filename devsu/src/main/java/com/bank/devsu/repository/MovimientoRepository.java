package com.bank.devsu.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bank.devsu.entity.Movimiento;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.cuentaId = :cuentaId ORDER BY m.fecha DESC")
    List<Movimiento> findByCuentaIdOrderByFechaDesc(@Param("cuentaId") Long cuentaId);

    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.cuentaId = :cuentaId AND m.fecha BETWEEN :inicio AND :fin ORDER BY m.fecha DESC")
    List<Movimiento> findByCuentaIdAndFechaBetweenOrderByFechaDesc(@Param("cuentaId") Long cuentaId, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT SUM(ABS(m.valor)) FROM Movimiento m " +
            "WHERE m.cuenta.cuentaId = :cuentaId " +
            "AND m.tipoMovimiento = 'Retiro' " +
            "AND m.fecha >= :inicioDia AND m.fecha <= :finDia")
     BigDecimal sumRetirosDelDia(@Param("cuentaId") Long cuentaId,
                                 @Param("inicioDia") LocalDateTime inicioDia,
                                 @Param("finDia") LocalDateTime finDia);
}
