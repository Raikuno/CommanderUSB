package com.usbcommander.server.utils;

import java.util.regex.Pattern;

/**
 * Clase de utilidad para aplicar las validaciones de los formularios en el backend
 */
public final class Validations {

    /**
     * Exrepsión regular a seguir por los emails
     */
    public static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    /**
     * Exrepsión regular a seguir por las contraseñas
     */
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
    /**
     * Mensaje de error a enviar en respuesta a contraseñas que no cumplan la expresión regular
     */
    public static final String PASSWORD_REQUIREMENTS = "Password must be at least 8 characters and include an uppercase letter, a lowercase letter and a digit";
    /**
     * Mensaje de error a enviar en respuesta a emails que no cumplan la expresión regular
     */
    public static final String EMAIL_REQUIREMENTS = "Email format is not valid";
    /**
     * Objeto pattern con la expresión regular del email
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    /**
     * Objeto pattern con la expresión regular de la contraseña
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    private Validations() {}

    /**
     * Permite validar un email en función de la expresión reegular descrita en esta clase
     * @param email El email a validar
     * @return Un booleano representando si el campo es correcto o no
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Permite validar una contraseña en función de la expresión reegular descrita en esta clase
     * @param password La contraseña a validar
     * @return Un booleano representando si el campo es correcto o no
     */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
}
