package com.usbcommander.server.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.usbcommander.server.entity.Session;
import com.usbcommander.server.entity.User;
import com.usbcommander.server.repository.SessionRepository;

@Service
/**
 * Implementación de ISessionService
 */
public class SessionService implements ISessionService {

    @Autowired 
    /**
     * El reepositorio vinculado al servicio
     */
    private SessionRepository sessionRepository;
    @Autowired 
    /**
     * El servicio de jwt que proporciona los métodos necesarios para tratar con estos
     */
    private IJwtService jwtService;

    private String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Session createSession(User user, String rawRefreshToken) {
        String jti = jwtService.getJtiFromToken(rawRefreshToken);
        Session session = new Session();
        session.setUser(user);
        session.setSelector(jti);
        session.setToken(hashToken(rawRefreshToken));
        session.setBlacklisted(false);
        return sessionRepository.save(session);
    }

    @Override
    public Optional<Session> findValidSession(String rawRefreshToken) {
        String jti = jwtService.getJtiFromToken(rawRefreshToken);
        if (jti == null) return Optional.empty();
        String hash = hashToken(rawRefreshToken);
        return sessionRepository.findBySelector(jti)
                .filter(s -> !s.getBlacklisted())
                .filter(s -> hash.equals(s.getToken()));
    }

    @Override
    public void invalidateSession(String rawRefreshToken) {
        String jti = jwtService.getJtiFromToken(rawRefreshToken);
        if (jti == null) return;
        sessionRepository.findBySelector(jti).ifPresent(s -> {
            s.setBlacklisted(true);
            sessionRepository.save(s);
        });
    }

    @Override
    public void invalidateAllUserSessions(User user) {
        sessionRepository.findByUser(user).forEach(s -> {
            s.setBlacklisted(true);
            sessionRepository.save(s);
        });
    }
}
