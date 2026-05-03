package com.usbcommander.server.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usbcommander.server.entity.Machine;
import java.util.List;


@Repository
/**
 * Interfaz que permite llevar a cabo las interacciones con la tabla 'machine' de la base de datos
 */
public interface MachineRepository extends JpaRepository<Machine, UUID>{
    /**
     * Permite encontrar una máquina en función de un id
     * @param id La id de la máquina a buscar
     * @return Un optional con la máquina encontrada o vacio en caso de no encontrar ninguna
     */
    Optional<Machine> findById(UUID id);
    /**
     * Permite encontrar una máquina en función de un nombre
     * @param name El nombre de la máquina a buscar
     * @return Un optional con la máquina encontrada o vacio en caso de no encontrar ninguna
     */
    Optional<Machine> findByName(String name);
    /**
     * Permite encontrar una máquina en función de un ip
     * @param ip El ip de la máquina a buscar
     * @return Un optional con la máquina encontrada o vacio en caso de no encontrar ninguna
     */
    Optional<Machine> findByIp(String ip);
    /**
     * Permite encontrar todas las máquinas en función de si estan habilitadas o no
     * @param disable SSi se desean encontrar las máquinas habilitadas (false) o deshabilitadas (true)
     * @return Una lista con todas las máquinas encontradas
     */
    List<Machine> findByDisable(Boolean disable);

}
