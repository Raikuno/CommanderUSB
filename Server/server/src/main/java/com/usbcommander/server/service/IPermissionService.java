package com.usbcommander.server.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.usbcommander.server.entity.Permission;

/**
 * Interfaz que establece los métodos necesarios de interacción con el repositorio PermissionRepository
 */
public interface IPermissionService {
    /**
     * Permite obtener todos los permisos almacenados en la base de datos mediante llamadas a los métodos del repositorio 
     * @return Una lista con todos los permisos almacenados en la base de datos
     */
    public List<Permission> getAll();
    /**
     * Permite obtener los datos de un permiso en función de una id mediante llamadas a los métodos del repositorio
     * @param id La id del permiso a buscar
     * @return Un optional con el permiso encontrado o vacio
     */
    public Optional<Permission> getById(UUID id);
    /**
     * Permite obtener los datos de un permiso en función de un nombre mediante llamadas a los métodos del repositorio
     * @param name El nombre del permiso a buscar
     * @return Un optional con el permiso encontrado o vacio
     */
    public Optional<Permission> getByName(String name);
}
