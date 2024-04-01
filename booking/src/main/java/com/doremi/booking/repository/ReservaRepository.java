package com.doremi.booking.repository;
import com.doremi.booking.dto.salida.reserva.ReservaSalidaDto;
import com.doremi.booking.entity.Instrumento;
import com.doremi.booking.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findReservasByInstrumento(Instrumento instrumento);


}
