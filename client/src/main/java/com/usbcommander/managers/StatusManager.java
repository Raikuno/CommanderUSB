package com.usbcommander.managers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Advapi32Util.EventLogIterator;
import com.sun.jna.platform.win32.Advapi32Util.EventLogRecord;
import com.usbcommander.AppConst;
import com.usbcommander.config.MachineConfig;
import com.usbcommander.config.contract.IMachineConfig;
import com.usbcommander.dto.LogDTO;
import com.usbcommander.managers.contract.IStatusManager;

public class StatusManager implements IStatusManager{
    private static StatusManager instance;

    private static final String INFOLOG_ERROR = "Error generating status log: ";
    private static final String WEIRDVALUE_ERROR = "WeirdValue found while parsing logs: ";
    private static final String INCOHERENTLOG_ERROR = "Error generating incoherent value log: ";
    private static final String REGISTRYMODIFICATIONLOG_ERROR = "Error generating registry modification log: ";
    private static final String UNAUTHORIZECONFIGMODLOG_ERROR = "Error generating unauthorized configuration modification log: ";
    private final String ENTRY_NAME = AppConst.EventLogReferences.ENTRY_NAME;
    private final String ENTRY_ROUTE = AppConst.EventLogReferences.ENTRY_ROUTE;
    private final int INFO_CODE = AppConst.EventLogReferences.INFO_CODE;
    private final int ERROR_CODE = AppConst.EventLogReferences.ERROR_CODE;
    private final int REGISTRY_MODIFICATION = AppConst.EventLogReferences.REGISTRY_MODIFICATION;
    private final int INCOHERENT_VALUE = AppConst.EventLogReferences.INCOHERENT_VALUE;
    private final int UNAUTHORIZED_CONFIGURATION_MODIFICATION = AppConst.EventLogReferences.UNAUTHORIZED_CONFIGURATION_MODIFICATION;

    private UsbMemoryManager usbMemoryManager;
    private IMachineConfig machineConfig;
    private ObjectMapper mapper;
    private Advapi32 advapi32;

    private StatusManager(){
        usbMemoryManager = UsbMemoryManager.getInstance();
        machineConfig = MachineConfig.getInstance();
        advapi32 = Advapi32.INSTANCE;
        mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static StatusManager getInstance(){
        if(instance == null){
            instance = new StatusManager();
        }
        return instance;
    }

    public Optional<LogDTO> errorLog(String message){
        LogDTO log = new LogDTO(ERROR_CODE, message, LocalDateTime.now());
        String logMessage;
        try {
            logMessage = mapper.writeValueAsString(log);
        } catch (JsonProcessingException e) {
            logMessage = "";
        }
        writeEventLog(WinNT.EVENTLOG_ERROR_TYPE, ERROR_CODE, logMessage);
        return Optional.of(log);
    }

    public Optional<LogDTO> infoLog(){
        LogDTO log = new LogDTO(
        usbMemoryManager.getAccessValue(), 
        machineConfig.getUsbEnable(), 
        usbMemoryManager.getConnectedDrives(), 
        LocalDateTime.now(), 
        INFO_CODE);
        
       try {
        writeEventLog(WinNT.EVENTLOG_INFORMATION_TYPE, INFO_CODE, mapper.writeValueAsString(log));
       } catch (JsonProcessingException e) {
        errorLog(INFOLOG_ERROR + e.getMessage());
       } 

        return Optional.of(log);    
    }

    public Optional<LogDTO> incoherentValueLog(){
        LogDTO log = new LogDTO(
            usbMemoryManager.getAccessValue(), 
            machineConfig.getUsbEnable(), 
            usbMemoryManager.getConnectedDrives(), 
            LocalDateTime.now(), 
            INCOHERENT_VALUE);
       try {
        writeEventLog(WinNT.EVENTLOG_WARNING_TYPE, INCOHERENT_VALUE, mapper.writeValueAsString(log));
       } catch (JsonProcessingException e) {
        errorLog(INCOHERENTLOG_ERROR + e.getMessage());
       } 

        return Optional.of(log);
    }

    public Optional<LogDTO> registryModificationLog(){
        LogDTO log = new LogDTO(
            usbMemoryManager.getAccessValue(), 
            machineConfig.getUsbEnable(), 
            usbMemoryManager.getConnectedDrives(), 
            LocalDateTime.now(), 
            REGISTRY_MODIFICATION);
       try {
        writeEventLog(WinNT.EVENTLOG_WARNING_TYPE, REGISTRY_MODIFICATION, mapper.writeValueAsString(log));
       } catch (JsonProcessingException e) {
        errorLog(REGISTRYMODIFICATIONLOG_ERROR + e.getMessage());
       }
        return Optional.of(log);
    }

    public Optional<LogDTO> unauthorizedConfigurationModificationLog(){
        LogDTO log = new LogDTO(
            usbMemoryManager.getAccessValue(), 
            machineConfig.getUsbEnable(), 
            usbMemoryManager.getConnectedDrives(), 
            LocalDateTime.now(), 
            UNAUTHORIZED_CONFIGURATION_MODIFICATION);
       try {
        writeEventLog(WinNT.EVENTLOG_WARNING_TYPE, UNAUTHORIZED_CONFIGURATION_MODIFICATION, mapper.writeValueAsString(log));
       } catch (JsonProcessingException e) {
        errorLog(UNAUTHORIZECONFIGMODLOG_ERROR + e.getMessage());
       }
        return Optional.of(log);
    }

    private void writeEventLog(int type, int code, String message){
        var handle = advapi32.RegisterEventSource(null, ENTRY_NAME);
        if(handle == null){
            return;
        }
        String [] text = { message };
        advapi32.ReportEvent(
            handle, 
            type,
            0, 
            code, 
            null, 
            1, 
            0, 
            text, 
            null);
        
        advapi32.DeregisterEventSource(handle);
    }

    @Override
    public List<LogDTO> getHistory() {
        List<LogDTO> eventos = new ArrayList<>();

        EventLogIterator iter = new EventLogIterator(null, ENTRY_ROUTE, WinNT.EVENTLOG_FORWARDS_READ);
        try {
            for (EventLogRecord record : iter) {
                if (!ENTRY_NAME.equalsIgnoreCase(record.getSource())) {
                    continue;
                }
                String mensaje = record.getStrings() != null && record.getStrings().length > 0
                        ? record.getStrings()[0]
                        : "";
                try {
                    mapper.readValue(mensaje, LogDTO.class);
                } catch (JsonProcessingException e) {
                    LogDTO log = new LogDTO(ERROR_CODE, WEIRDVALUE_ERROR + e.getMessage(), LocalDateTime.now());
                    eventos.add(log);
                }
            }
        } finally {
            iter.close(); 
        }

        return eventos;

    }      

    public void deleteHistory(){

    }
}
