package com.doremi.booking.service;

import com.doremi.booking.dto.entrada.instrumento.InstrumentoEntradaDto;
import com.doremi.booking.dto.entrada.modificacion.InstrumentoModificacionEntradaDto;
import com.doremi.booking.dto.salida.instrumento.InstrumentoSalidaDto;
import com.doremi.booking.entity.Instrumento;
import com.doremi.booking.exceptions.ResourceNotCreatedException;
import com.doremi.booking.exceptions.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface IInstrumentoService {

    InstrumentoSalidaDto agregarInstrumento(InstrumentoEntradaDto instrumento) throws ResourceNotCreatedException;

    List<InstrumentoSalidaDto> listarInstrumentos();

    List<InstrumentoSalidaDto> listarInstrumentosHome();

    void eliminarInstrumento(Long id) throws ResourceNotFoundException;

    InstrumentoSalidaDto buscarInstrumentoPorNombre(String nombre) throws ResourceNotFoundException;

    InstrumentoSalidaDto buscarInstrumentoPorId(Long id) throws ResourceNotFoundException;

    InstrumentoSalidaDto modificarInstrumento(InstrumentoModificacionEntradaDto instrumentoModificado) throws ResourceNotCreatedException, ResourceNotFoundException;


    List<InstrumentoSalidaDto> buscarInstrumentosPorNombre(String nombre) throws ResourceNotFoundException;

    List<InstrumentoSalidaDto> buscarInstrumentosDisponibles(String palabraClave, LocalDate fechaInicial, LocalDate fechaFinal) throws ResourceNotFoundException;
}