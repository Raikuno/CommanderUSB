package com.usbcommander.services.contract;

/**
 * Clase abstracta usada como base para la creación de las clases "Servicio" que llevarán a cabo el control de todas las acciones de la aplicación
 */
public abstract class CommanderService extends Thread{
    /**
     * Variable que almacena si el Hilo del servicio esta en ejecución o no
     */
    protected boolean running;


    protected CommanderService(){
    }

    @Override
    public abstract void run();

    @Override
    public synchronized void start() {
        if(!running){
            running = true;
            super.start();
        }
    }

    /**
     * Método utilizado para detener el servicio correctamente
     */
    public void stopService(){
        if(running){
            this.running = false;
        }
    }
}
