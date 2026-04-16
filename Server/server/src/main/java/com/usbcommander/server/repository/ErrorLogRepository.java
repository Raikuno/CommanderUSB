package com.usbcommander.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.usbcommander.server.entity.ErrorLog;
import com.usbcommander.server.entity.Machine;

import java.util.List;
import java.time.LocalDateTime;



public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long>{
    List<ErrorLog> findByMachine(Machine machine);
    List<ErrorLog> findByRecievedDate(LocalDateTime recievedDate);
    List<ErrorLog> findByRecievedDateBetween(LocalDateTime start, LocalDateTime end);
}
