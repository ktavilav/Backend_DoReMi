package com.doremi.booking.repository;

import com.doremi.booking.dto.salida.instrumento.InstrumentoSalidaDto;
import com.doremi.booking.entity.Instrumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InstrumentoRepository extends JpaRepository<Instrumento, Long> {
    Instrumento findByNombre(String nombre);
    List<Instrumento> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT i FROM Instrumento i " +
            "WHERE LOWER(i.nombre) LIKE LOWER(CONCAT('%', :palabraClave, '%')) " +
            "AND NOT EXISTS (" +
            "    SELECT 1 FROM Reserva r " +
            "    WHERE r.instrumento = i " +
            "    AND (" +
            "        :fechaInicial BETWEEN r.fechaInicial AND r.fechaFinal " +
            "        OR :fechaFinal BETWEEN r.fechaInicial AND r.fechaFinal " +
            "        OR r.fechaInicial BETWEEN :fechaInicial AND :fechaFinal" +
            "    )" +
            ")")
    List<Instrumento> findAvailableInstrumentosByKeywordAndDates(
            @Param("palabraClave") String palabraClave,
            @Param("fechaInicial") LocalDate fechaInicial,
            @Param("fechaFinal") LocalDate fechaFinal
    );
}



