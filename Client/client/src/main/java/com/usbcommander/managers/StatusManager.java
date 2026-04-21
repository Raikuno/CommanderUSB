package com.usbcommander.managers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Advapi32Util.EventLogIterator;
import com.sun.jna.platform.win32.Advapi32Util.EventLogRecord;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.usbcommander.AppConst;
import com.usbcommander.config.MachineConfig;
import com.usbcommander.config.contract.IMachineConfig;
import com.usbcommander.dto.LogDTO;
import com.usbcommander.enums.LogType;
import com.usbcommander.managers.contract.IStatusManager;
import com.usbcommander.managers.contract.IUsbMemoryManager;

public class StatusManager extends IStatusManager{
    private IUsbMemoryManager usbMemoryManager;
    private IMachineConfig machineConfig;
    private ObjectMapper mapper;
    private Advapi32 advapi32;

    private StatusManager(){
        usbMemoryManager = UsbMemoryManager.getInstance();
        machineConfig = MachineConfig.getInstance();
        advapi32 = Advapi32.INSTANCE;
        mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static IStatusManager getInstance(){
        if(instance == null){
            instance = new StatusManager();
        }
        return instance;
    }

    public LogDTO generateLog(LogType type){
        return generateLog(type, "");
    }

    public LogDTO generateLog(LogType type, String message){
        LogDTO log;
        String logJson;
        int winType;
        if(type == LogType.ERROR || type == LogType.CONNECTION){
            log = new LogDTO(type.getCode(), message, LocalDateTime.now());
        } else {
            try{
                log = new LogDTO(
                    usbMemoryManager.getAccessValue(), 
                    machineConfig.isUsbEnable(), 
                    usbMemoryManager.getConnectedDrives(), 
                    LocalDateTime.now(),
                    type.getCode()
                );
            } catch (Win32Exception err){
                log = new LogDTO(
                    type.getCode(), 
                    AppConst.ErrorMessages.LOG_GENERATION_ERROR, 
                    LocalDateTime.now());
            }
        }

        switch (type) {
            case INFO:
                    winType = WinNT.EVENTLOG_INFORMATION_TYPE;
                break;

            case CONFIG_MOD:
                    winType = WinNT.EVENTLOG_WARNING_TYPE;
                break;

            case REGISTRY_MOD:
                    winType = WinNT.EVENTLOG_WARNING_TYPE;
                break;

            case INCOHERENT:
                    winType = WinNT.EVENTLOG_WARNING_TYPE;
                break;

            case MEMORY_CONN:
                    winType = WinNT.EVENTLOG_WARNING_TYPE;
                break;

            case CONNECTION:
                    winType = WinNT.EVENTLOG_ERROR_TYPE;
                break;

            case ERROR:
                    winType = WinNT.EVENTLOG_ERROR_TYPE;
                break;

            default:
                    winType = WinNT.EVENTLOG_ERROR_TYPE;
                break;
        }


        try {
            logJson = mapper.writeValueAsString(log);
            writeEventLog(winType, type.getCode(), logJson);
        } catch (JsonProcessingException e) {
            
        }

        return log;
    }

    private void writeEventLog(int type, int code, String message){
        var handle = advapi32.RegisterEventSource(
            null,        
            AppConst.EventLogReferences.ENTRY_NAME
        );

        if (handle == null) {
            return;
        }

        try {
            String[] text = { message };
            advapi32.ReportEvent(
                handle,
                type,
                0,
                code,
                null,
                1,
                0,
                text,
                null
            );
        } finally {
            advapi32.DeregisterEventSource(handle);
        }
    }
     
    @Override
    public List<LogDTO> getHistory() {
        List<LogDTO> history = new ArrayList<>();

        EventLogIterator iter = new EventLogIterator(
            null, 
            AppConst.EventLogReferences.ENTRY_NAME, 
            WinNT.EVENTLOG_FORWARDS_READ);

        try {
            for (EventLogRecord record : iter) {
                String message = record.getStrings() != null && record.getStrings().length > 0
                        ? record.getStrings()[0]: "";
                try {
                    LogDTO log = mapper.readValue(message, LogDTO.class);
                    history.add(log);
                } catch (JsonProcessingException e) {
                    LogDTO log = new LogDTO(
                        AppConst.EventLogReferences.ERROR_CODE, 
                        AppConst.ErrorMessages.WEIRDVALUE_ERROR + e.getMessage(), 
                        LocalDateTime.now());
                    history.add(log);
                }
            }
        } finally {
            iter.close(); 
        }

        return history;

    }      

    @Override
    public void deleteHistory(){
        HANDLE handle = advapi32.OpenEventLog(
            null, 
            AppConst.EventLogReferences.ENTRY_NAME);

        if(handle == null){
            return;
        }

        try{
            boolean success = advapi32.ClearEventLog(handle, null);
            if(!success){
                generateLog(LogType.ERROR, AppConst.ErrorMessages.ERROR_DELETE);
            }
        }catch(Win32Exception e){
            generateLog(LogType.ERROR, AppConst.ErrorMessages.ERROR_DELETE + e.getMessage());
        } finally{
            advapi32.CloseEventLog(handle);
        }
    }
}
