package com.usbcommander.server.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usbcommander.server.entity.User;

import java.util.Optional;


@Repository
/**
 * Interfaz que permite llevar a cabo las interacciones con la tabla 'user' de la base de datos
 */
public interface UserRepository extends JpaRepository<User, UUID>{
    /**
     * Permite encontrar un usuario en función de su nombre
     * @param name El nombre del usuario a buscar
     * @return Un optional con el usuario encontrado o vacio en caso de no encontrar ninguno 
     */
    Optional<User> findByName(String name);
    /**
     * Permite encontrar un usuario en función de su id
     * @param id El id del usuario a buscar
     * @return Un optional con el usuario encontrado o vacio en caso de no encontrar ninguno 
     */
    Optional<User> findById(UUID id);
    /**
     * Permite encontrar un usuario en función de su email
     * @param email El email del usuario a buscar
     * @return Un optional con el usuario encontrado o vacio en caso de no encontrar ninguno 
     */
    Optional<User> findByEmail(String email);

}
