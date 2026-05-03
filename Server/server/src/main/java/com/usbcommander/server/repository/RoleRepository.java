package com.usbcommander.server.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usbcommander.server.entity.Role;

@Repository
/**
 * Interfaz que permite llevar a cabo las interacciones con la tabla 'role' de la base de datos
 */
public interface RoleRepository extends JpaRepository<Role, UUID>{
    /**
     * Permite encontrar un rol en función de su nombre
     * @param name El nombre del permiso a buscar
     * @return Un optional con el rol encontrado o vacio en caso de no encontrar ninguno 
     */
    Optional<Role> findByName(String name);
    /**
     * Permite encontrar un rol en función de su id
     * @param id El id del rol a buscar
     * @return Un optional con el rol encontrado o vacio en caso de no encontrar ninguno 
     */
    Optional<Role> findById(UUID uuid);
}
