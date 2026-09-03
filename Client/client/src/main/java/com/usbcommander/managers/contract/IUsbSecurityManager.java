package com.usbcommander.managers.contract;

import java.util.function.Consumer;


public abstract class IUsbSecurityManager {
    public abstract Thread setUsbListener(Runnable execute, Consumer<Exception> onCatch);
    public abstract Thread setConfigListener(Runnable execute, Consumer<Exception> onCatch);
    /**
     * Compara los valores de configuración almacenados en memoria con los almacenados en la máquina
     * @return True si los valores coinciden, false en el caso opuesto
     */
    public abstract boolean configMatchesMemory(boolean isUsbEnable, long logFrecuency, String ip, Integer port);
    /**
     * Compara el estado actual del permiso de uso de las memorias usb en el registro de windows con el valor de configuración de la aplicación almacenado en memoria
     * @return True si el registro coincide con el valor de configuración, y falso en el caso contrario
    */
    public abstract boolean usbStorMatchesMemory(boolean isUsbEnable, int usbAccessValue);
}
