package com.usbcommander.enums;

public enum LogType {
    INFO(1001), 
    CONFIG_MOD(1002),
    REGISTRY_MOD(1003), 
    INCOHERENT(1004), 
    MEMORY_CONN(1005),
    CONNECTION(1006), 
    ERROR(1007);

    private int code;

    private LogType(int code){
        this.code = code;
    }

    public int getCode(){
        return this.code;
    }
}
