package com.doremi.booking.entity;

import com.doremi.booking.listeners.ReservaListener;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@EntityListeners(ReservaListener.class)
@NoArgsConstructor
@Data
@Entity
@Table(name = "RESERVAS")

public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserva_id")
    private Long idReserva;

    private LocalDate fechaReserva;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "instrumento_id" , referencedColumnName = "instrumento_id")
    private Instrumento instrumento;

    private LocalDate fechaInicial;

    private LocalDate fechaFinal;


    public Reserva(LocalDate fechaReserva, User usuario, Instrumento instrumento, LocalDate fechaInicial, LocalDate fechaFinal) {
        this.fechaReserva = fechaReserva;
        this.usuario = usuario;
        this.instrumento = instrumento;
        this.fechaInicial = fechaInicial;
        this.fechaFinal = fechaFinal;
    }

    public void setInstrumento(Instrumento instrumento) {
        this.instrumento = instrumento;
    }

    public Instrumento getInstrumento() {
        return instrumento;


    }
}

