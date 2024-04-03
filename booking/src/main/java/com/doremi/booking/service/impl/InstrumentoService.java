package com.doremi.booking.service.impl;

import com.doremi.booking.dto.entrada.imagen.ImagenEntradaDto;
import com.doremi.booking.dto.entrada.instrumento.InstrumentoEntradaDto;
import com.doremi.booking.dto.entrada.modificacion.InstrumentoModificacionEntradaDto;
import com.doremi.booking.dto.salida.categoria.CategoriaSalidaDto;
import com.doremi.booking.dto.salida.imagen.ImagenSalidaDto;
import com.doremi.booking.dto.salida.instrumento.InstrumentoSalidaDto;
import com.doremi.booking.entity.Categoria;
import com.doremi.booking.entity.Imagen;
import com.doremi.booking.entity.Instrumento;
import com.doremi.booking.exceptions.ResourceNotCreatedException;
import com.doremi.booking.exceptions.ResourceNotFoundException;
import com.doremi.booking.repository.CategoriaRepository;
import com.doremi.booking.repository.ImagenRepository;
import com.doremi.booking.repository.InstrumentoRepository;
import com.doremi.booking.service.IInstrumentoService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class InstrumentoService implements IInstrumentoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentoService.class);

    private final InstrumentoRepository instrumentoRepository;

    private final CategoriaRepository categoriaRepository;

    private final ImagenRepository imagenRepository;

    private final ModelMapper modelMapper;

    public InstrumentoService(InstrumentoRepository instrumentoRepository, CategoriaRepository categoriaRepository, ImagenRepository imagenRepository, ModelMapper modelMapper) {
        this.instrumentoRepository = instrumentoRepository;
        this.categoriaRepository = categoriaRepository;
        this.imagenRepository = imagenRepository;
        this.modelMapper = modelMapper;
    }
    @Transactional
    @Override
    public InstrumentoSalidaDto agregarInstrumento(InstrumentoEntradaDto instrumento) throws ResourceNotCreatedException {
        if ((instrumentoRepository.findByNombre(instrumento.getNombre()) == null)) {

            Categoria categoria = categoriaRepository.findById(instrumento.getCategoria())
                    .orElseThrow(() -> new ResourceNotCreatedException("La categoría no existe"));

            LOGGER.info("Categoria -> " + categoria);
            Instrumento instrumentoARegistrar = maptoEntity(instrumento);

            instrumentoARegistrar.setCategoria(categoria);

            Instrumento instrumentoAgregado = instrumentoRepository.save(instrumentoARegistrar);

            LOGGER.info("instrumento agregado -> " + instrumentoAgregado);

            List<Imagen> imagen = imagenRepository.saveAll(mapToEntityImagenList(instrumento.getImagen(), instrumentoAgregado.getInstrumento_id()));
            LOGGER.info("Imagen guardada -> " + imagen);

            instrumentoAgregado.setImagenes(imagen);

            InstrumentoSalidaDto instrumentoSalidaDto = maptoDtoSalida(instrumentoAgregado);

            LOGGER.info("Instrumento guardado: {}", instrumentoSalidaDto);

            return instrumentoSalidaDto;
        } else {

            LOGGER.info("El instrumento ya se encuentra en la base de datos");
            throw new ResourceNotCreatedException("El instrumento ya se encuentra en la base de datos");
        }

    }

    @Override
    public List<InstrumentoSalidaDto> listarInstrumentos() {
        List<Instrumento> listaInstrumentos = instrumentoRepository.findAll();

        LOGGER.info("Listado de instrumentos: {}", listaInstrumentos);
        return listaInstrumentos.stream().map(this::maptoDtoSalida).toList();
    }

    @Override
    public void eliminarInstrumento(Long id) throws ResourceNotFoundException{
        Instrumento instrumentoABuscar = instrumentoRepository.findById(id).orElse(null);

        if (instrumentoABuscar != null) {
            try {
                instrumentoRepository.deleteById(id);
                LOGGER.warn("Se eliminó el instrumento con id: {}", id);
            }catch (DataIntegrityViolationException e) {
                    throw new DataIntegrityViolationException("No se puede eliminar el instrumento porque tiene reservas asociadas.");
            }
        } else {
            LOGGER.error("Instrumento no encontrado con id: {}", id);
            throw new ResourceNotFoundException("El instrumento no se encuentra con el id " + id);
        }

    }

    @Override
    public InstrumentoSalidaDto buscarInstrumentoPorNombre(String nombre) throws ResourceNotFoundException {
        Instrumento instrumentoABuscar = instrumentoRepository.findByNombre(nombre);

        if (instrumentoABuscar != null) {
            LOGGER.info("El instrumento {} se ha encontrado.", nombre);
            return mapEntitytoDtoSalida(instrumentoABuscar);
        } else {
            LOGGER.error("No se encuentra el instrumento con el nombre: {}", nombre);
            throw new ResourceNotFoundException("El instrumento no se encuentra con el nombre " + nombre);
        }
    }


    @Override
    public InstrumentoSalidaDto buscarInstrumentoPorId(Long id) throws ResourceNotFoundException {
        Instrumento instrumentoABuscar = instrumentoRepository.findById(id).orElse(null);
        if (instrumentoABuscar != null) {
            return mapEntitytoDtoSalida(instrumentoABuscar);
        } else {
            LOGGER.error("No se encuentra el instrumento con el ID: {}", id);
            throw new ResourceNotFoundException("El instrumento no se encuentra con el id " + id);
        }
    }

    @Override
    public InstrumentoSalidaDto modificarInstrumento(InstrumentoModificacionEntradaDto instrumentoModificado) throws ResourceNotCreatedException, ResourceNotFoundException {
        InstrumentoSalidaDto instrumentoSalidaDto = null;

        if (buscarInstrumentoPorId(instrumentoModificado.getInstrumento_id()) != null) {

            Categoria categoria = categoriaRepository.findById(instrumentoModificado.getCategoria())
                    .orElseThrow(() -> new ResourceNotCreatedException("La categoría no existe"));

            LOGGER.info ("Categoria -> " + categoria);
            Instrumento instrumentoARegistrar = mapDtoModificadoToEntity(instrumentoModificado);

            instrumentoARegistrar.setCategoria(categoria);

            Instrumento instrumentoAgregado = (instrumentoRepository.save (instrumentoARegistrar));

            LOGGER.info("El instrumento: {}, fue modificado exitosamente", instrumentoAgregado);

            List<Imagen> imagen = imagenRepository.saveAll (mapToEntityImagenList(instrumentoModificado.getImagen(),instrumentoAgregado.getInstrumento_id()));
            LOGGER.info("Imagen guardada -> " + imagen);

            instrumentoAgregado.setImagenes(imagen);

            instrumentoSalidaDto = maptoDtoSalida(instrumentoAgregado);

            LOGGER.info("Instrumento guardado: {}", instrumentoSalidaDto);


        }else{
            LOGGER.error(" El instrumento: {}, no fue modificado porque no se encontró", instrumentoSalidaDto);
            throw new ResourceNotCreatedException("El instrumento no se pudo modificar porque no se encuentra en la base de datos");
        }
        return instrumentoSalidaDto;
    }

    @Override
    public List<InstrumentoSalidaDto> buscarInstrumentosPorNombre(String nombre) throws ResourceNotFoundException {
        List<Instrumento> instrumentosEncontrados = instrumentoRepository.findByNombreContainingIgnoreCase(nombre);

        if(!instrumentosEncontrados.isEmpty()){
            LOGGER.info("Estos instrumentos coinciden con la búsqueda");
            return instrumentosEncontrados.stream().map(this::maptoDtoSalida).toList();
        }else{
            LOGGER.info("No existen coincidencias con el nombre ingresado");
            throw new ResourceNotFoundException("No existen coincidencias con el nombre ingresado");
        }
    }

    @Override
    public List<InstrumentoSalidaDto> listarInstrumentosHome() {
        List<Instrumento> listaInstrumentos = instrumentoRepository.findAll();
        Collections.shuffle(listaInstrumentos);
        List<Instrumento> instrumentosAleatorios = listaInstrumentos.subList(0, Math.min(10, listaInstrumentos.size()));
        LOGGER.info("Listado de instrumentos aleatorios: {}", instrumentosAleatorios);
        return instrumentosAleatorios.stream().map(this::maptoDtoSalida).toList();
    }

    @Override
    public List<InstrumentoSalidaDto> buscarInstrumentosDisponibles(String palabraClave, LocalDate fechaInicial, LocalDate fechaFinal) throws ResourceNotFoundException {
        List<Instrumento> instrumentosDisponibles = instrumentoRepository.findAvailableInstrumentosByKeywordAndDates(palabraClave, fechaInicial, fechaFinal);
        if(!instrumentosDisponibles.isEmpty()){
            LOGGER.info("Listado de instrumentos: {}", instrumentosDisponibles);
            return instrumentosDisponibles.stream().map(this::maptoDtoSalida).toList();
        }else{
            LOGGER.info("No existen coincidencias con el nombre ingresado");
            throw new ResourceNotFoundException("No existen coincidencias con el nombre ingresado");
        }

    }

    public Instrumento maptoEntity(InstrumentoEntradaDto instrumentoEntradaDto) {
        return modelMapper.map(instrumentoEntradaDto, Instrumento.class);
    }

    public List<Imagen> mapToEntityImagenList(List<ImagenEntradaDto> imagenEntradaDtoList, Long idInstrumento) {

        imagenEntradaDtoList = imagenEntradaDtoList.stream()
                .map(imagenEntradaDto -> {
                    imagenEntradaDto.setInstrumento_id(idInstrumento);
                    return imagenEntradaDto;
                })
                .collect(Collectors.toList());

        return imagenEntradaDtoList
                .stream()
                .map(imagenEntradaDto -> {
                    Imagen imagen = new Imagen();
                    imagen.setTitulo(imagenEntradaDto.getTitulo());
                    imagen.setUrl(imagenEntradaDto.getUrl());
                    imagen.setInstrumento(imagenEntradaDto.getInstrumento_id());
                    return imagen;
                })
                .collect(Collectors.toList());
    }


    public InstrumentoSalidaDto maptoDtoSalida(Instrumento instrumento) {
        InstrumentoSalidaDto instrumentoSalidaDto = new InstrumentoSalidaDto();
        instrumentoSalidaDto.setInstrumento_id(instrumento.getInstrumento_id());
        instrumentoSalidaDto.setNombre(instrumento.getNombre());
        instrumentoSalidaDto.setCategoria(new CategoriaSalidaDto(instrumento.getCategoria().getCategoria_id(),instrumento.getCategoria().getNombre(), instrumento.getCategoria().getDescripcion(), instrumento.getCategoria().getImagen()));
        instrumentoSalidaDto.setDescripcion(instrumento.getDescripcion());
        instrumentoSalidaDto.setPrecioDia(instrumento.getPrecioDia());

        List<ImagenSalidaDto> imagenSalidaDto = modelMapper.map(instrumento.getImagenes(), new TypeToken<List<ImagenSalidaDto>>() {}.getType());
        instrumentoSalidaDto.setImagen(imagenSalidaDto);

        return instrumentoSalidaDto;
    }


    public Instrumento mapDtoModificadoToEntity(InstrumentoModificacionEntradaDto instrumentoModificado){
        return modelMapper.map(instrumentoModificado, Instrumento.class);
    }

    public InstrumentoSalidaDto mapEntitytoDtoSalida(Instrumento instrumento){
        InstrumentoSalidaDto instrumentoSalida = modelMapper.map(instrumento, InstrumentoSalidaDto.class);
        instrumentoSalida.setImagen(instrumento.getImagenes().stream()
                .map(imagen -> new ImagenSalidaDto(imagen.getImagen_id(), imagen.getTitulo(), imagen.getUrl()))
                .collect(Collectors.toList()));

        return instrumentoSalida;
    }




}