package com.bank.devsu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bank.devsu.entity.Cuenta;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

    @Query("SELECT c FROM Cuenta c WHERE c.cliente.personaId = :personaId")
    List<Cuenta> findByClientePersonaId(@Param("personaId") Long personaId);

    List<Cuenta> findByNumeroCuentaContainingIgnoreCase(String numeroCuenta);
}
