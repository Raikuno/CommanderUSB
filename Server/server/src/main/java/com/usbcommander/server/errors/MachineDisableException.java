package com.usbcommander.server.errors;

/**
 * Excepción creada con el fin de ser lanzada en caso de que una máquina marcada como deshabilitada trate de conectarse al servidor
 */
public class MachineDisableException extends Exception{
    public MachineDisableException(String mess){
        super(mess);
    }
}
