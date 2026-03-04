package com.usbcommander.manager.contract;

import java.util.HashSet;
import java.util.Set;

public abstract class Service implements Runnable{
    public static Set<Service> serviceList = new HashSet<>();
    protected boolean running;


    private Service(){}

    @Override
    public abstract void run();

    public boolean startService(){
        try{
            run();
            running = true;
            Service.serviceList.add(this);
            return true;
        } catch(Exception ex){
            return false;
        }
    }

    @Override
    public abstract int hashCode();
}
