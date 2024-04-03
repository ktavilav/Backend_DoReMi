package com.doremi.booking.service.impl;

import com.doremi.booking.dto.entrada.modificacion.InstrumentoModificacionEntradaDto;
import com.doremi.booking.dto.entrada.reseva.ReservaEntradaDto;
import com.doremi.booking.dto.salida.Usuario.UsuarioSalidaDTO;
import com.doremi.booking.dto.salida.instrumento.InstrumentoSalidaDto;
import com.doremi.booking.dto.salida.reserva.InstrumentoReservaSalidaDto;
import com.doremi.booking.dto.salida.reserva.ReservaSalidaDto;
import com.doremi.booking.dto.salida.reserva.UsuarioReservaSalidaDto;
import com.doremi.booking.entity.Instrumento;
import com.doremi.booking.entity.Reserva;
import com.doremi.booking.entity.User;
import com.doremi.booking.exceptions.BadRequestException;
import com.doremi.booking.exceptions.ResourceNotFoundException;
import com.doremi.booking.repository.InstrumentoRepository;
import com.doremi.booking.repository.ReservaRepository;
import com.doremi.booking.service.IReservaService;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReservaService implements IReservaService {

    private final Logger LOGGER = LoggerFactory.getLogger(ReservaService.class);

    private final ReservaRepository reservaRepository;

    private final ModelMapper modelMapper;

    private InstrumentoService instrumentoService;

    private UsuarioService usuarioService;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository, ModelMapper modelMapper, InstrumentoService instrumentoService, UsuarioService usuarioService) {
        this.reservaRepository = reservaRepository;
        this.modelMapper = modelMapper;
        this.instrumentoService = instrumentoService;
        this.usuarioService = usuarioService;
    }


    

   /*@Override
    public Boolean fechasDisponibles(Long instrumentoId, LocalDate fechaInicial, LocalDate fechaFinal){
        List<Reserva> reservas = reservaRepository.findByInstrumento_IdAndFechaInicialBetweenAndFechaFinalBetween(instrumentoId, fechaInicial, fechaFinal);
        return reservas.isEmpty();
    }*/



    @Override
    public ReservaSalidaDto reservarInstrumento(ReservaEntradaDto reservaEntradaDto) throws BadRequestException, ResourceNotFoundException {
        ReservaSalidaDto reservaSalidaDto = null;
        UsuarioSalidaDTO usuario = usuarioService.buscarUsuarioPorId(reservaEntradaDto.getUsuarioId());InstrumentoSalidaDto instrumento = instrumentoService.buscarInstrumentoPorId(reservaEntradaDto.getInstrumentoId());

        List<Reserva> reservasParaInstrumento = reservaRepository.findReservasByInstrumento(maptoDtoSalidaAInstrumento(instrumento));

        List<Reserva> reservasOcupadas = reservasParaInstrumento.stream()
                .filter(reserva -> {
                    return !(reservaEntradaDto.getFechaFinal().isBefore(reserva.getFechaInicial()) ||
                            reservaEntradaDto.getFechaInicial().isAfter(reserva.getFechaFinal()));
                })
                .collect(Collectors.toList());

        if(instrumento == null || usuario == null){
            if(instrumento == null && usuario == null){
                LOGGER.error("El usuario y el instrumento ingresados no se encuentran en la base de datos");
                throw new BadRequestException("El usuario y el instrumento ingresados no se encuentran en la base de datos");
            }else if (instrumento == null){
                LOGGER.error ("El instrumento ingresado no se encuentra en la base de datos");
                throw new BadRequestException("El instrumento ingresado no se encuentra en la base de datos");
            }else{
                LOGGER.error ("El usuario ingresado no se encuentra en la base de datos");
            }
        }
       if(!reservasOcupadas.isEmpty()){
            LOGGER.info("Las fechas deseadas no están disponibles. Por favor ingresa otro rango de fechas");
            throw new BadRequestException("Las fechas deseadas no están disponibles. Por favor ingresa otro rango de fechas");
        }
        else{
            Reserva reservaNueva = reservaRepository.save(mapDtoEntradaAEntidad(reservaEntradaDto));
            reservaSalidaDto = entidadADtoSalida(reservaNueva);
            LOGGER.info("Reserva registrada con exito: {}", reservaSalidaDto);
        }
        return reservaSalidaDto;
    }
    @Transactional
    @Override
    public List<ReservaSalidaDto> listarReservas() {
        List<Reserva> listaReservas = reservaRepository.findAll();
        LOGGER.info("Listado de reservas: {} ", listaReservas);

        return listaReservas.stream()
                .map(reserva -> {
                    try {
                        return entidadADtoSalida(reserva);
                    } catch (ResourceNotFoundException e) {
                        LOGGER.error("Error al convertir reserva a DTO: {}", e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
    @Transactional
    @Override
    public List<ReservaSalidaDto> buscarReservasPorInstrumento(Long instrumentoId) throws ResourceNotFoundException {

        InstrumentoSalidaDto instrumento = instrumentoService.buscarInstrumentoPorId(instrumentoId);
        List<Reserva> reservasInstrumento = reservaRepository.findReservasByInstrumento(maptoDtoSalidaAInstrumento(instrumento));

        if(!reservasInstrumento.isEmpty()){
            return reservasInstrumento.stream()
                    .map(reserva ->{
                        try {
                            return entidadADtoSalida(reserva);
                        }catch (ResourceNotFoundException e){
                            LOGGER.error("Error al convertir reserva a DTO: {}", e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
        } else{
            LOGGER.error("El instrumento con el ID: {} no tiene reservas", instrumentoId);
            throw new ResourceNotFoundException("El instrumento con el ID: " +instrumentoId + " no tiene reservas");
        }
    }

    public Reserva mapDtoEntradaAEntidad(ReservaEntradaDto reservaEntradaDto) throws ResourceNotFoundException {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(null);

        UsuarioSalidaDTO usuarioSalidaDto = usuarioService.buscarUsuarioPorId(reservaEntradaDto.getUsuarioId());
        reserva.setUsuario(maptoDtoSalidaAUsuario(usuarioSalidaDto));

        InstrumentoSalidaDto instrumentosalidaDto = instrumentoService.buscarInstrumentoPorId(reservaEntradaDto.getInstrumentoId());
        reserva.setInstrumento(maptoDtoSalidaAInstrumento(instrumentosalidaDto));

        reserva.setFechaInicial(reservaEntradaDto.getFechaInicial());
        reserva.setFechaFinal(reservaEntradaDto.getFechaFinal());

        return reserva;
    }

    private UsuarioReservaSalidaDto usuarioSalidaDtoASalidaReservaDto(Long id) throws ResourceNotFoundException {
        return modelMapper.map(usuarioService.buscarUsuarioPorId(id), UsuarioReservaSalidaDto.class);
    }

    private InstrumentoReservaSalidaDto instrumentoSalidaDtoASalidaReservaDto(Long id) throws ResourceNotFoundException {
        return modelMapper.map(instrumentoService.buscarInstrumentoPorId(id), InstrumentoReservaSalidaDto.class);
    }


    private ReservaSalidaDto entidadADtoSalida(Reserva reserva) throws ResourceNotFoundException {
        ReservaSalidaDto reservaSalidaDto = modelMapper.map(reserva, ReservaSalidaDto.class);
        reservaSalidaDto.setUsuarioReservaSalidaDto(usuarioSalidaDtoASalidaReservaDto(reserva.getUsuario().getUsuario_id()));
        reservaSalidaDto.setInstrumentoReservaSalidaDto(instrumentoSalidaDtoASalidaReservaDto(reserva.getInstrumento().getInstrumento_id()));
        return reservaSalidaDto;
    }

    private User maptoDtoSalidaAUsuario(UsuarioSalidaDTO usuarioSalidaDTO) {
        return modelMapper.map(usuarioSalidaDTO, User.class);
    }

    private Instrumento maptoDtoSalidaAInstrumento(InstrumentoSalidaDto instrumentoSalidaDTO) {
        return modelMapper.map(instrumentoSalidaDTO, Instrumento.class);
    }
    private InstrumentoSalidaDto maptoDtoSalidaAInstrumentoDisponible(Instrumento instrumento) {
        return modelMapper.map(instrumento, InstrumentoSalidaDto.class);
    }

      
    



}
