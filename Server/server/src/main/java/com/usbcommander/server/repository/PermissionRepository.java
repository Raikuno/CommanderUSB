package com.usbcommander.server.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usbcommander.server.entity.Permission;

@Repository
/**
 * Interfaz que permite llevar a cabo las interacciones con la tabla 'permissions' de la base de datos
*/
public interface PermissionRepository extends JpaRepository<Permission, UUID>{
    /**
     * Permite encontrar un permiso en función de su id
     * @param id El id del permiso a buscar
     * @return Un optional con el permiso encontrado o vacio en caso de no encontrar ninguno
     */
    Optional<Permission> findById(UUID id);
    /**
     * Permite encontrar un permiso en función de su nombre
     * @param name El nombre del permiso a buscar
     * @return Un optional con el permiso encontrado o vacio en caso de no encontrar ninguno
     */
    Optional<Permission> findByName(String name);
}
