package com.usbcommander.config.contract;

public interface IMachineConfig {
    
    public boolean saveConfig();

    public boolean getUsbEnable();

    public long getLogFrecuency();

    public void setUsbEnable(boolean usbEnable);

    public void setLogFrecuency(long logFrecuency);
}
