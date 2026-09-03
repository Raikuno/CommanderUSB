package com.usbcommander.managers.contract;

import java.util.List;
import java.util.Map;

import com.usbcommander.dto.LogDTO;
import com.usbcommander.enums.LogType;

/**
 * Clase abstracta usada como base para la creación de las clases encargadas de crear los registros del estado de la máquina
 */
public abstract class IStatusManager {

    /**
     * Método utilizado para generar y almacenar un registro automáticamente, evaluando el estado de la máquina y su estado esperado/deseado
     * @return El registro generado
     */
    public abstract LogDTO generateLog(int usbAccessValue, List<Map<String, String>> connectedDrives);

    /**
     * Método utilizado para generar y almacenar un registro en función de un tipo definido y un mensaje específico. utilizado específicamente para los registros de error
     * @param type El tipo del registro a crear
     * @param message El mensaje a almacenar en el registro
     * @return El registro generado
     */
    public abstract LogDTO generateLog(LogType type, String message, int usbAccessValue, List<Map<String, String>> connectedDrives);

    public abstract LogDTO generateErrorLog(String message); 

    /**
     * Método utilizado para obtener todos los registros almacenados
     * @return Una lista con todos los registros actualmente almacenados
     */
    public abstract List<LogDTO> getHistory();

    /**
     * Método utilizado para eliminar los registros almacenados
     */
    public abstract void deleteHistory();
}
