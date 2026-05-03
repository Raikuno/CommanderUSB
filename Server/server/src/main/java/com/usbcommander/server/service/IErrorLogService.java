package com.usbcommander.server.service;

import java.util.List;

import com.usbcommander.server.entity.ErrorLog;
import com.usbcommander.server.entity.Machine;

/**
 * Interfaz que establece los métodos necesarios de interacción con el repositorio ErrorLogRepository
 */
public interface IErrorLogService {
    /**
     * Permite obtener los logs de error en función de una máquina mediante llamadas a los métodos del repositorio
     * @param machine La máquina cuyos errorlog se quieren recuperar
     * @return Una lista con los errorlog relacionados a la máquina descrita
     */
    public List<ErrorLog> getByErrorLogsByMachine(Machine machine);
    /**
     * Permite almmacenar un errorlog en la base de datos
     * @param errorLog El nuevo errorLog a almacenar
     */
    public void save(ErrorLog errorLog);
}
