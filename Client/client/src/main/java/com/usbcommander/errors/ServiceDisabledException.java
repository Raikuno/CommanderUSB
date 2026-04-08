package com.usbcommander.errors;

public class ServiceDisabledException extends Exception{
    public ServiceDisabledException(String mess){
        super(mess);
    }
}
