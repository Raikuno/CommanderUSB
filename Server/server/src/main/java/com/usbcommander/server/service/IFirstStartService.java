package com.usbcommander.server.service;

/**
 * IInterfaz  encargada de establecer los métodos a usar en relación a las acciones a tomar en el caso de que se esté creando la primera cuenta de la aplicación
 */
public interface IFirstStartService {
    /**
     * Método encargado de revisar si existe o no una cuenta de administrador creada
     * @return Un booleano representando si la cuenta existe o no
     */
    public boolean adminAccountCreated();
    /**
     * Método encargado de crear la cuenta de administrador
     * @param email El email de la cuenta de administrador
     * @param password La contraseña de la cuenta de administrador
     * @param name El nombre de la cuenta de administrador
     */
    public void createAdminAccount(String email, String password, String name);
}
