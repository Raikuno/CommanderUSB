package com.usbcommander.services.contract;

/**
 * Abstract class to use as a base for the creation of the "Serrrvice" classes that will be used to control the actions of the application.
 * It also contains a Set were the different Services will be added after starting it's execution.
 */
public abstract class CommanderService extends Thread{
    protected boolean running;


    protected CommanderService(){
    }

    @Override
    public abstract void run();

    /**
     * Start the thread using the method of the parent class (Thread) if it is not already running, mark the object as running and add it to the list of services
     */
    @Override
    public synchronized void start() {
        if(!running){
            running = true;
            super.start();
        }
    }

    public void stopService(){
        if(running){
            this.running = false;
        }
    }
}
