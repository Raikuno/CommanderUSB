package com.usbcommander.config.contract;

public abstract class IMachineConfig {
    public static IMachineConfig instance;
    
    public abstract boolean saveConfig();

    public abstract boolean getUsbEnable();

    public abstract long getLogFrecuency();

    public abstract int getPort();

    public abstract String getIp();

    public abstract void setUsbEnable(boolean usbEnable);

    public abstract void setLogFrecuency(long logFrecuency);

    public abstract void enableServerService();
}
