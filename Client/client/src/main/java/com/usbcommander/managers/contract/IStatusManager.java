package com.usbcommander.managers.contract;

import java.util.List;

import com.usbcommander.dto.LogDTO;
import com.usbcommander.enums.LogType;

/**
 * Clase abstracta usada como base para la creación de las clases encargadas de crear los registros del estado de la máquina
 */
public abstract class IStatusManager {

    /**
     * Almacena la instancia de la clase usada en la aplicación. Este será inicializado por las clases que definan los métodos de la clase abstracta
     */
    protected static IStatusManager instance;

    protected IStatusManager(){}

    /**
     * Método utilizado para generar y almacenar un registro automáticamente, evaluando el estado de la máquina y su estado esperado/deseado
     * @return El registro generado
     */
    public abstract LogDTO generateLog();

    /**
     * Método utilizado para generar y almacenar un registro en función de un tipo definido
     * @param type El tipo del registro a crear
     * @return El registro generado
     */
    public abstract LogDTO generateLog(LogType type);

    /**
     * Método utilizado para generar y almacenar un registro en función de un tipo definido y un mensaje específico. utilizado específicamente para los registros de error
     * @param type El tipo del registro a crear
     * @param message El mensaje a almacenar en el registro
     * @return El registro generado
     */
    public abstract LogDTO generateLog(LogType type, String message);

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
