package com.usbcommander.managers.contract;


public interface IStatusManager {
    public void errorLog(String message);

    public void infoLog();

    public void incoherentValueLog();

    public void registryModificationLog();

    public void unauthorizedConfigurationModificationLog();
}
