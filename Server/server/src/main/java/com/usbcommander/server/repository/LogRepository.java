package com.usbcommander.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usbcommander.server.entity.Log;
import java.util.List;
import com.usbcommander.server.entity.Machine;


@Repository
/**
 * Interfaz que permite llevar a cabo las interacciones con la tabla 'log' de la base de datos
 */
public interface LogRepository extends JpaRepository<Log, Long>{
    /**
     * Permite obtener todos los Log asignados a una missma máquina
     * @param machine La máquina cuyos logs se desean encontrar
     * @return Lista con todos los Log encontrados asignados a la máquina
     */
    List<Log> findByMachine(Machine machine);
    /**
     * Permite obtener todos los logs de un mismo logCode
     * @param logCode El código del tipo de log que se desea recuperar
     * @return Una lista con todos los logs de ese tipo
     */
    List<Log> findByLogCode(Integer logCode);
    /**
     * Permite obtener todos los logs que requieran o no de una revisión
     * @param needsRevission Si se requieren de los logs que necesiten (true) o no (false) revisión
     * @return Una lista con los logs encontrados
     */
    List<Log> findByNeedsRevission(Boolean needsRevission);
    /**
     * Permite encontar todos los logs de una misma máquina que requieran o no de revisión
     * @param machine La máquina cuyos logs se desean encontrar 
     * @param needsRevission Si se requieren de los logs que necesiten (true) o no (false) revisión
     * @return Una lista con los logs encontrados
     */
    List<Log> findByMachineAndNeedsRevission(Machine machine, Boolean needsRevission);
}
