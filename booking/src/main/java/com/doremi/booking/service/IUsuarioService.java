package com.doremi.booking.service;

import java.util.List;

import com.doremi.booking.dto.entrada.modificacion.UsuarioCambioRoleEntradaDTO;
import com.doremi.booking.dto.salida.Usuario.UsuarioSalidaDTO;
import com.doremi.booking.exceptions.ResourceNotCreatedException;
import com.doremi.booking.exceptions.ResourceNotFoundException;


public interface IUsuarioService {

    List<UsuarioSalidaDTO> listarUsuarios();
    UsuarioSalidaDTO cambiarRole(UsuarioCambioRoleEntradaDTO CambioRole) throws ResourceNotCreatedException, ResourceNotFoundException;
    UsuarioSalidaDTO buscarUsuarioPorId(Long id) throws ResourceNotFoundException;
    UsuarioSalidaDTO buscarUsuarioPorUsername(String username) throws ResourceNotFoundException;
} 
