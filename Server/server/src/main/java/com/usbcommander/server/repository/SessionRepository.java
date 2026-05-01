package com.usbcommander.server.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.usbcommander.server.entity.Session;
import com.usbcommander.server.entity.User;

import java.util.Optional;
import java.util.List;



public interface SessionRepository extends JpaRepository<Session, UUID>{
    Optional<Session> findBySelector(String selector);
    List<Session> findByUser(User user);
}
