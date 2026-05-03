package com.usbcommander.server.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usbcommander.server.entity.Session;
import com.usbcommander.server.entity.User;

import java.util.Optional;
import java.util.List;

@Repository
/**
 * Interfaz que permite llevar a cabo las interacciones con la tabla 'session' de la base de datos
 */
public interface SessionRepository extends JpaRepository<Session, UUID>{
    /**
     * Permite encontrar una sesión en función de un selector
     * @param selector El selector a partir del cuál buscar la sesión
     * @return Un optional con la sesión encontrada o vacio en caso de no encontrar ninguna
     */
    Optional<Session> findBySelector(String selector);
    /**
     * Permite encontrar todas las sesiones de un mismo usuario
     * @param user El usuario a partir del cuál buscar las sesiones
     * @return Una lista con todas las sesiones encontradas
     */
    List<Session> findByUser(User user);
}
