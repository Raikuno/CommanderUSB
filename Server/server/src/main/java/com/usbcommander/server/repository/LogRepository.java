package com.usbcommander.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usbcommander.server.entity.Log;
import java.util.List;
import com.usbcommander.server.entity.Machine;
import java.time.LocalDateTime;


@Repository
public interface LogRepository extends JpaRepository<Log, Integer>{
    List<Log> findByMachine(Machine machine);
    List<Log> findByLogCode(Integer logCode);
    List<Log> findByRecievedDateBetweenAndMachine(LocalDateTime start, LocalDateTime end, Machine machine);
}
