package com.usbcommander.server.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.usbcommander.server.entity.Machine;

/**
 * Interfaz que establece los métodos necesarios de interacción con el repositorio MachineRepository
 */
public interface IMachineService {
    /**
     * Permite obtener todas las máquinas de la base de datos mediante llamadas a los métodos del repositorio
     * @return
     */
    public List<Machine> getAll();
    
    /**
     * Permite obtener los datos de una máquina a partir de su id mediante llamadas a los métodos del repositorio
     * @param id La id de la máquina a buscar
     * @return La máquina encontrada o un optional vacio
     */
    public Optional<Machine> getById(UUID id);

    /**
     * Permite obtener los datos de una máquina a partir de su nombre mediante llamadas a los métodos del repositorio
     * @param name El nombre de la máquina a buscar
     * @return La máquina encontrada o un optional vacio
     */
    public Optional<Machine> getByName(String name);

    /**
     * Permite obtener una máquina a partir de una ip mediante llamadas a los métodos del reepositorio
     * @param ip La ip de la máquina a buscar
     * @return La máquina encontrada o un optional vacio
     */
    public Optional<Machine> getByIp(String ip);

    /**
     * Permite obtener todas las máquinas habilitadas o deshabilitadas mediante llamadas a los métodos del repositorio
     * @param enable Si se desean encontrar las máquinas habilitadas (true) o deshabilitadas (false)
     * @return Una lista con todas las máquinas encontradas
     */
    public List<Machine> getByEnable(Boolean enable);

    /**
     * Permite almacenar una nueva máquina en la base de datos
     * @param machine Los datos de la nueva máquina a almacenar
     */
    public void save(Machine machine);
}
