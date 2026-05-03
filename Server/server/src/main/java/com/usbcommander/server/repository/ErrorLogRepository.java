package com.usbcommander.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usbcommander.server.entity.ErrorLog;
import com.usbcommander.server.entity.Machine;

import java.util.List;

@Repository
/**
 * Interfaz que permite llevar a cabo las interacciones con la tabla 'error_log' de la base de datos
 */
public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long>{
    /**
     * Permite obtener todos los errorLog asignados a una missma máquina
     * @param machine La máquina cuyos error_logs se desean encontrar
     * @return Lista con todos los ErrorLog encontrados asignados a la máquina
     */
    List<ErrorLog> findByMachine(Machine machine);
}
