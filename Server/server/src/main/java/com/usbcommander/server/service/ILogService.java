package com.usbcommander.server.service;

import java.util.List;
import java.util.Optional;

import com.usbcommander.server.entity.Log;
import com.usbcommander.server.entity.Machine;

/**
 * Interfaz que establece los métodos necesarios de interacción con el repositorio LogRepository
 */
public interface ILogService {
    /**
     * Permite obtener los logs en función de una máquina mediante llamadas a los métodos del repositorio
     * @param machine La máquina cuyos log se quieren recuperar
     * @return Una lista con los log relacionados a la máquina descrita
     */
    public List<Log> getByMachine(Machine machine);
    /**
     * Permite obtener los logs en función del código de log deseado, mediante llamadas a los métodos del repositorio
     * @param logCode El código de los logs a buscar
     * @return Una lista con los log encontrados
     */
    public List<Log> getByLogCode(Integer logCode);
    /**
     * Permite obtener los logs que necesiten de una revisión mediante llamadas a los métodos del repositorio
     * @return Una lista con los log encontrados
     */
    public List<Log> getAllUnrevised();
    /**
     * Permite obtener todos los logs de una máquina que necesiten revisión
     * @param machine La máquina de la cuál se quieren obtener los logs
     * @param needsRevission Si se desean obtener los logs que necesiten o no de una revisión
     * @return Una lista con los log encontrados
     */
    public List<Log> getByMachineAndNeedsRevission(Machine machine, Boolean needsRevission);
    /**
     * Permite obtener un log a partir de su id mediante llamadas a los métodos del repositorio
     * @param id La id del log a buscar
     * @return El log encontrado o un Optional vacio
     */
    public Optional<Log> getById(Long id);
    /**
     * Permite marcar cierto número de logs como revisados
     * @param ids Lista con los ids de los logs a revisar
     */
    public void reviseAll(List<Long> ids);
    /**
     * Permite almacenar un nuevo log en la base de datos
     * @param log
     */
    public void save(Log log);
}
