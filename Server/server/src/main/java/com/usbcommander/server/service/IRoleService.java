package com.usbcommander.server.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.usbcommander.server.entity.Role;

/**
 * Interfaz que establece los métodos necesarios de interacción con el repositorio RoleRepository
 */
public interface IRoleService {

    /**
     * Permite obtener todos los roles almacenados en la base de datos mediante llamadas a los métodos del repositorio 
     * @return Una lista con todos los roles almacenados en la base de datos
     */
    public List<Role> getAll();
    /**
     * Permite obtener los datos de un rol en función de un nombre mediante llamadas a los métodos del repositorio
     * @param name El nombre del rol a buscar
     * @return Un optional con el rol encontrado o vacio
     */
    public Optional<Role> getByName(String name);
    /**
     * Permite obtener los datos de un rol en función de una id mediante llamadas a los métodos del repositorio
     * @param id La id del rol a buscar
     * @return Un optional con el rol encontrado o vacio
     */
    public Optional<Role> getById(UUID uuid);
    /**
     * Permite almacenar un nuevo rol en la base de datos mediatne llamadas a los métodos del repositorio
     * @param role El nuevo rol a almacenar
     */
    public void save(Role role);
}
