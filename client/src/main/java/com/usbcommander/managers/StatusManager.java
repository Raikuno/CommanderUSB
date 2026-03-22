package com.usbcommander.managers;

import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.WinNT;
import com.usbcommander.AppConst;
import com.usbcommander.config.MachineConfig;
import com.usbcommander.dto.LogDTO;
import com.usbcommander.managers.contract.IStatusManager;

public class StatusManager implements IStatusManager{
    private static StatusManager instance;

    private final String ENTRY_NAME = AppConst.EventLogReferences.ENTRY_NAME;
    private final int INFO_CODE = AppConst.EventLogReferences.INFO_CODE;
    private final int ERROR_CODE = AppConst.EventLogReferences.ERROR_CODE;
    private final int REGISTRY_MODIFICATION = AppConst.EventLogReferences.REGISTRY_MODIFICATION;
    private final int INCOHERENT_VALUE = AppConst.EventLogReferences.INCOHERENT_VALUE;
    private final int UNAUTHORIZED_CONFIGURATION_MODIFICATION = AppConst.EventLogReferences.UNAUTHORIZED_CONFIGURATION_MODIFICATION;

    private UsbMemoryManager usbMemoryManager;
    private MachineConfig machineConfig;
    private ObjectMapper mapper;

    private StatusManager(){
        usbMemoryManager = UsbMemoryManager.getInstance();
        machineConfig = MachineConfig.getInstance();
        mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static StatusManager getInstance(){
        if(instance == null){
            instance = new StatusManager();
        }
        return instance;
    }

    public void errorLog(String message){
        writeEventLog(WinNT.EVENTLOG_ERROR_TYPE, ERROR_CODE, message);
    }

    public void infoLog(){
        LogDTO log = new LogDTO(
            usbMemoryManager.getAccessValue(), 
            machineConfig.getUsbEnable(), 
            usbMemoryManager.getConnectedDrives(), 
            LocalDateTime.now(), 
            INFO_CODE);
       try {
        writeEventLog(WinNT.EVENTLOG_INFORMATION_TYPE, INFO_CODE, mapper.writeValueAsString(log));
       } catch (JsonProcessingException e) {
        errorLog("Error generating status log:\n" + e.getMessage());
       } 
    }

    public void incoherentValueLog(){
        LogDTO log = new LogDTO(
            usbMemoryManager.getAccessValue(), 
            machineConfig.getUsbEnable(), 
            usbMemoryManager.getConnectedDrives(), 
            LocalDateTime.now(), 
            INCOHERENT_VALUE);
       try {
        writeEventLog(WinNT.EVENTLOG_WARNING_TYPE, INCOHERENT_VALUE, mapper.writeValueAsString(log));
       } catch (JsonProcessingException e) {
        errorLog("Error generating incoherent value log:\n" + e.getMessage());
       } 
    }

    public void registryModificationLog(){
        LogDTO log = new LogDTO(
            usbMemoryManager.getAccessValue(), 
            machineConfig.getUsbEnable(), 
            usbMemoryManager.getConnectedDrives(), 
            LocalDateTime.now(), 
            REGISTRY_MODIFICATION);
       try {
        writeEventLog(WinNT.EVENTLOG_WARNING_TYPE, REGISTRY_MODIFICATION, mapper.writeValueAsString(log));
       } catch (JsonProcessingException e) {
        errorLog("Error generating registry modification log:\n" + e.getMessage());
       }
    }

    public void unauthorizedConfigurationModificationLog(){
        LogDTO log = new LogDTO(
            usbMemoryManager.getAccessValue(), 
            machineConfig.getUsbEnable(), 
            usbMemoryManager.getConnectedDrives(), 
            LocalDateTime.now(), 
            UNAUTHORIZED_CONFIGURATION_MODIFICATION);
       try {
        writeEventLog(WinNT.EVENTLOG_WARNING_TYPE, UNAUTHORIZED_CONFIGURATION_MODIFICATION, mapper.writeValueAsString(log));
       } catch (JsonProcessingException e) {
        errorLog("Error generating unauthorized configuration modification log:\n" + e.getMessage());
       }
    }

    private void writeEventLog(int type, int code, String message){
        var handle = Advapi32.INSTANCE.RegisterEventSource(null, ENTRY_NAME);
        if(handle == null){
            
            return;
        }
        String [] text = { message };
        Advapi32.INSTANCE.ReportEvent(
            handle, 
            type,
            0, 
            code, 
            null, 
            1, 
            0, 
            text, 
            null);
        
        Advapi32.INSTANCE.DeregisterEventSource(handle);
    }
}
