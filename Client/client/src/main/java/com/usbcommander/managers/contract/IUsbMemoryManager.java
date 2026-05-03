package com.usbcommander.managers.contract;

import java.util.List;
import java.util.Map;


/**
 * Clase abstracta usada como base para la creación de las clases encargadas de manipular la máquina para interactuar con las memorias usb 
 */
public abstract class IUsbMemoryManager {
    /**
     * Almacena la instancia de la clase usada en la aplicación. Este será inicializado por las clases que definan los métodos de la clase abstracta
     */
    protected static IUsbMemoryManager instance;
    
    protected IUsbMemoryManager(){}

    /**
     * Este método se encarga de revisar las unidades de almmacenamiento usb conectadas a la máquina y devuelve una lista con el nombre, la letra asignada a la unidad y el serial de cada unidad
     * @return Una lista con el nombre, la letra asignada a la unidad y el serial de cada unidad conecatada a la máquina
     */
    public abstract List<Map<String, String>> getConnectedDrives();

    /**
     * Revisa si existe alguna unidad de memoria usb conectada a la máquina
     * @return El número de memorias conectadas a la máquina
     */
    public abstract int isDriveConnected();

    /**
     * Este método revisa si la máquina permite o no la conexión de memorias usb
     * @return Un int que define si es posible o no conectar memorias usb
     */
    public abstract int getAccessValue();

    /**
     * Este método sirve para permitir la conexión de memorias usb en la máquina
     */
    public abstract void enableAccess();

    /**
     * Este método sirve para prohibir la conexión de memorias usb en la máquina
     */
    public abstract void disableAccess();

    /**
     * Este método tiene como finalidad, desmontar todas las unidades de memoria usb conectadas a la máquina
     */
    public abstract void removeExternalDrives();
}