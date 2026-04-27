package com.bank.devsu.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bank.devsu.dto.ReporteDTO;
import com.bank.devsu.service.ReporteService;

@RestController
@RequestMapping("/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping
    public ResponseEntity<?> generarReporte(
            @RequestParam Long personaId,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime inicio = LocalDateTime.parse(fechaInicio, formatter);
            LocalDateTime fin = LocalDateTime.parse(fechaFin, formatter);

            ReporteDTO reporte = reporteService.generarReporte(personaId, inicio, fin);
            return ResponseEntity.ok(reporte);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al generar reporte");
        }
    }
}
