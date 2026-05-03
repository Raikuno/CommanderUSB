package com.usbcommander.server.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.usbcommander.server.entity.User;

/**
 * Interfaz que establece los métodos necesarios de interacción con el repositorio UserrRepository
 */
public interface IUserService {
    /**
     * Permite obtener todos los usuarios de la base de datos mediante llamadas a los métodos del repositorio
     * @return Una lista con todos los usuarios encontrados en la base de datos 
     */
    public List<User> getAll();
    /**
     * Permite obtener los datos de un usuario a partir de su nombre mediante llamadas a los métodos del repositorio
     * @param name El nombre del usuario a buscar
     * @return El usuario encontrado o un optional vacio
     */
    public Optional<User> getByName(String name);
    /**
     * Permite obtener los datos de un usuario a partir de su email mediante llamadas a los métodos del repositorio
     * @param email El email del usuario a buscar
     * @return El usuario encontrado o un optional vacio
     */
    public Optional<User> getByEmail(String email);
    /**
     * Permite obtener los datos de un usuario a partir de su id mediante llamadas a los métodos del repositorio
     * @param name El id del usuario a buscar
     * @return El usuario encontrado o un optional vacio
     */
    public Optional<User> getById(UUID id);
    /**
     * Permite actualizar un usuario sin dañar la contraseña de este
     * @param user El usuario cuyas credenciales se pretenden actualizar
     */
    public void update(User user);
    /**
     * Permite actualizar la contraseña de un usuario
     * @param user El usuario cuya contraseña se pretende actualizar
     * @param newPassword La nueva contraseña del usuario
     */
    public void updatePassword(User user, String newPassword);
    /**
     * Permite crear un nuevo usuario, aplicando la encriptación necesaria a la contraseña
     * @param user El nuevo usuario a crear
     */
    public void create(User user);
}
