package com.usbcommander.services;

import com.usbcommander.services.contract.CommanderService;
import com.usbcommander.static_classes.RegistryInteractor;

public class SecurityService extends CommanderService{

    private static SecurityService instance;

    public static SecurityService getInstance(){
        if(instance == null){
            instance = new SecurityService();
        }
        return instance;
    }

    /**
     * This method enables the use of usb storage units during the specified duration
     * @param time The time during the usb storage units will be able to be mounted
     * @return  A boolean representing if the action was succesfull or not. This will only be returned after the access has been closed
     */
    public boolean openAccess(long time){
        boolean enabled = RegistryInteractor.enableAccess();
        if (!enabled){
            return enabled;
        }
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            RegistryInteractor.disableAccess();
            return false;
        }

        RegistryInteractor.disableAccess();
        return true;
    }

    /**
     * This method forces to close the access to usb storage units
     * @return A boolean representing if the operation was succesful
     */
    public boolean forceClose(){
        boolean closed = RegistryInteractor.disableAccess();
        return closed;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hashCode'");
    }
    
}
