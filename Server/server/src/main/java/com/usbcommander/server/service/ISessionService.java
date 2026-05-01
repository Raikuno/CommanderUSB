package com.usbcommander.server.service;

import java.util.Optional;

import com.usbcommander.server.entity.Session;
import com.usbcommander.server.entity.User;

public interface ISessionService {
    Session createSession(User user, String rawRefreshToken);
    Optional<Session> findValidSession(String rawRefreshToken);
    void invalidateSession(String rawRefreshToken);
    void invalidateAllUserSessions(User user);
}
