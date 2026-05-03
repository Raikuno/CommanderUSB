package com.usbcommander.errors;

/**
 * Excepción definida para ser lanzada en caso de que se llame a un método de una de las clases que extiendan a CommanderService mientras que su respectivo hilo no este funcionando.
 */
public class ServiceDisabledException extends Exception{
    public ServiceDisabledException(String mess){
        super(mess);
    }
}
