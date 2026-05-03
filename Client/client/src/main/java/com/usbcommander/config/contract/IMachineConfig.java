package com.usbcommander.config.contract;

/**
 * Clase abstracta usada como base para configurar métodos y controlar valores en relación a la configuración de la aplicación en la máquina
 */
public abstract class IMachineConfig {
    /**
     * Almacena la instancia de configuración usada en la aplicación. Este será inicializado por las clases que definan los métodos de la clase abstracta
     */
    protected static IMachineConfig instance;
    
    /**
     * Este método guarda la configuración actualmente en memoria
     * @return
     */
    public abstract boolean saveConfig();

    /**
     * Este método tiene como finalidad saber si debería ser posible montar unidades de memoria usb extraibles en la máquina
     * @return 
     */
    public abstract boolean isUsbEnable();

    /**
     * Este método devuelve la frecuencia almacenada en memoria con la que se envían los registros automáticos 
     * @return 
     */
    public abstract long getLogFrecuency();

    /**
     * Este método devuelve el puerto del servidor configurado
     * @return
     */
    public abstract int getPort();

    /**
     * Este método devuelve la ip del servidor configurado
     * @return
     */
    public abstract String getIp();

    /**
     * Este método habilita o deshabilita la configuración de si una unidad usb de memoria extraible debería de poder montarse o no
     * @param usbEnable Si se deberían poder (true) o no (false) poder montar memorias usb
     */
    public abstract void setUsbEnable(boolean usbEnable);

    /**
     * Este método se utiliza para configurar la frecuencia con la que la máquina debería generar los registros automáticos
     * @param logFrecuency La nueva frecuencia en milesimas de segundo
     */
    public abstract void setLogFrecuency(long logFrecuency);

    /**
     * Este método se utiliza para habilitar la conexión con el servidor
     */
    public abstract void enableServerService();
}
