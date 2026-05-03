package com.usbcommander.server.service;

import java.util.Optional;

import com.usbcommander.server.entity.Session;
import com.usbcommander.server.entity.User;

/**
 * Interfaz que establece los métodos necesarios para hacer un correcto uso de los refresh tokens
 */
public interface ISessionService {
    /**
     * Permite crear una nueva sesión en la base de datos a partir de un refresh token y un usuario
     * @param user El usuario vinculado a la sesion
     * @param rawRefreshToken El refresh token de la sesión
     * @return Un objeto session a partir de los datos introducidos
     */
    Session createSession(User user, String rawRefreshToken);
    /**
     * Permitee buscar una sesión váilda a partir de un token
     * @param rawRefreshToken El token a buscar en la base de datos para verificar su validez
     * @return Un optional con el objeto de sesión o vacio si esta no se encuentra
     */
    Optional<Session> findValidSession(String rawRefreshToken);
    /**
     * Permite invalidar una sesión a partir de un refresh token
     * @param rawRefreshToken El refresh token vinculado a la sesión a invalidar
     */
    void invalidateSession(String rawRefreshToken);
    /**
     * Permite invalidar todas las sesiones de un usuario
     * @param user El usuario cuyas sesiones se van a invalidar
     */
    void invalidateAllUserSessions(User user);
}
