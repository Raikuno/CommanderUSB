package com.usbcommander.services.contract;

import java.util.HashSet;
import java.util.Set;

public abstract class CommanderService implements Runnable{
    public static Set<CommanderService> serviceList = new HashSet<>();
    protected boolean running;


    protected CommanderService(){}

    @Override
    public abstract void run();

    public boolean startService(){
        try{
            run();
            running = true;
            CommanderService.serviceList.add(this);
            return true;
        } catch(Exception ex){
            return false;
        }
    }

    @Override
    public abstract int hashCode();
}
