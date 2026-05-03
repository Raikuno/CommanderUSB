package com.usbcommander.errors;

/**
 * Excepción definida para ser lanzada en caso de que un método de la clase ServerConnectionService sea llamado mientras que no haya una conexión establecida con el servidor
 */
public class NotConnectionException extends Exception{
    public NotConnectionException(String mess){
        super(mess);
    }
}