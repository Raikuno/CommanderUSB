package com.usbcommander.server.service;

import com.usbcommander.server.entity.User;

/**
 * Interfaz encargada de establecer los métodos necesarios a usar en la aplicacióñ en relación a la implementación de Jwt (json web token), incluyendo los refreshtoken y los access token
 */
public interface IJwtService {

    /**
     * Método encargado de generar un access token en función de un usuario
     * @param user La cuenta de usuario para la que se generará el token
     * @return El token de acceso para la cuenta de usuario
     */
    public String generateAccessToken(User user);
    /**
     * Método encargado de generar un refresh token en función de un usuario 
     * @param user La cuenta de usuario para la que se generará el token
     * @return El refresh token para la cuenta de usuario
     */
    public String generateRefreshToken(User user);
    /**
     * Método encargado de descifrar el email de un token dado
     * @param token El token del que se quiere obtener el email
     * @return El email descifrado del token
     */
    public String getEmailFromToken(String token);
    /**
     * Método encargado de realizar las operaciones necesarias para validar el jwt sea este un refresh token o un accesstoken
     * @param token El token a validar
     * @return Un booleano representando si el token es válido o no
     */
    public boolean validateToken(String token);
    /**
     * Método encargado de verificar si un token es del tipo "refresh token"
     * @param token El token a revisar
     * @return Un booleano representando si un token es del tipo refresh token o no
     */
    public boolean isRefreshToken(String token);
    /**
     * Método encargado de obtener el Jti de un token, el cuál  es usado como selector en la basse de datos
     * @param token El token del que se quiere obtener el jti
     * @return El jti del token, si es que este tiene uno
     */
    public String getJtiFromToken(String token);
    /**
     * Método encargado de obtener el tiempo de vida configurado de loss refresh token
     * @return El tiempo de vida configurado de loss refresh token
     */
    public long getRefreshTokenSeconds();
    /**
     * Método encargado de obtener el tiempo de vida configurado de loss access token
     * @return El tiempo de vida configurado de loss access token
     */
    public long getAccessTokenSeconds();
}
