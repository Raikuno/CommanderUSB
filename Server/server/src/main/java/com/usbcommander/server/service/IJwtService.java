package com.usbcommander.server.service;

import com.usbcommander.server.entity.User;

public interface IJwtService {

    public String generateAccessToken(User user);
    public String generateRefreshToken(User user);
    public String getEmailFromToken(String token);
    public boolean validateToken(String token);
    public boolean isRefreshToken(String token);
}
