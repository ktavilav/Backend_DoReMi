package com.doremi.booking.repository;

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

    

}


