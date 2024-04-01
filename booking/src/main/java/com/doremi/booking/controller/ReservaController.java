package com.doremi.booking.controller;

import com.doremi.booking.dto.entrada.instrumento.InstrumentoEntradaDto;
import com.doremi.booking.dto.entrada.reseva.ReservaEntradaDto;
import com.doremi.booking.dto.salida.instrumento.InstrumentoSalidaDto;
import com.doremi.booking.dto.salida.reserva.ReservaSalidaDto;
import com.doremi.booking.exceptions.BadRequestException;
import com.doremi.booking.exceptions.ResourceNotFoundException;
import com.doremi.booking.service.IReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private IReservaService reservaService;

    @Autowired

    public ReservaController(IReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @Operation(summary = "Registro de una nueva reserva")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva agregada correctamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservaSalidaDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)
    })
    @PostMapping("agregar")
    public ResponseEntity<ReservaSalidaDto> agregarInstrumento(@Valid @RequestBody ReservaEntradaDto reserva) throws BadRequestException, ResourceNotFoundException {
        return new ResponseEntity<>(reservaService.reservarInstrumento(reserva), HttpStatus.CREATED);
    }

    @Operation(summary = "Listado de reservas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de reservas obtenido correctamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservaSalidaDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)
    })
    @GetMapping("listar")
    public ResponseEntity<List<ReservaSalidaDto>> listarReservas() throws ResourceNotFoundException {
        return new ResponseEntity<>(reservaService.listarReservas(), HttpStatus.OK);
    }

    @Operation(summary = "Buscar reservas por Id del instrumento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservas encontradas exitosamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservaSalidaDto.class))}),
            @ApiResponse(responseCode = "400", description = "Id no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Reservas no encontradas",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)
    })
    @GetMapping("buscarPorInstrumento/{idInstrumento}")
    public ResponseEntity<List<ReservaSalidaDto>> buscarReservasPorInstrumento(@PathVariable Long idInstrumento) throws ResourceNotFoundException{
       return new ResponseEntity<>(reservaService.buscarReservasPorInstrumento(idInstrumento), HttpStatus.OK);
    }
}
