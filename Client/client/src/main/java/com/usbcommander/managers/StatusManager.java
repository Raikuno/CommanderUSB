package com.usbcommander.managers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Advapi32Util.EventLogIterator;
import com.sun.jna.platform.win32.Advapi32Util.EventLogRecord;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.usbcommander.AppConst;
import com.usbcommander.config.contract.IMachineConfig;
import com.usbcommander.dto.LogDTO;
import com.usbcommander.enums.LogType;
import com.usbcommander.managers.contract.IStatusManager;

/**
 * Implementación de IStatusManager para funciionarr en un entorno de windows haciendo uso del registro de windows y windows event viewer
 */
@Singleton
public class StatusManager extends IStatusManager{
    /**
     * Instancia de IMachineConfig para comparar el estado esperado de la máquina
     */
    private IMachineConfig machineConfig;
    /**
     * Instancia de Jackson para serializar objetos de registro como json
     */
    private ObjectMapper mapper;
    /**
     * Instancia de Advapi32 para interactuar con el registro de windows y windows event viewer
     */
    private Advapi32 advapi32;

    /**
     * Constructor de la clase en el que se inicializan las propiedades del objeto
     */
    @Inject
    public StatusManager(IMachineConfig machineConfig){
        advapi32 = Advapi32.INSTANCE;
        mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.machineConfig = machineConfig;
    }

    @Override
    public LogDTO generateLog(int usbAccessValue, List<Map<String, String>> connectedDrives){
        Optional<LogType> type = Optional.empty();
        if(machineConfig.isUsbEnable() && usbAccessValue==AppConst.RegistryReferences.ENABLE_VALUE){
            type = Optional.of(LogType.INCOHERENT);
        }

        if(!connectedDrives.isEmpty()){
            type = Optional.of(LogType.MEMORY_CONN);
        }

        if(type.isEmpty()){
            type = Optional.of(LogType.INFO);
        }

        return generateLog(type.get(), "", usbAccessValue, connectedDrives);
    }

    @Override
    public LogDTO generateErrorLog(String message){
        return generateLog(LogType.ERROR, message, -1, Collections.emptyList()); 
    }

    @Override
    public LogDTO generateLog(LogType type, String message, int usbAccessValue, List<Map<String, String>> connectedDrives){
        LogDTO log;
        String logJson;
        int winType;
        if(type == LogType.ERROR || type == LogType.CONNECTION){
            log = new LogDTO(type.getCode(), message, LocalDateTime.now());
        } else {
            try{
                log = new LogDTO(
                    usbAccessValue, 
                    machineConfig.isUsbEnable(), 
                    connectedDrives,
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

    /**
     * Método usado para facilitar la escritura de los objetos de registro en windows event viewer.
     * @param type El tipo de registro de windows a almmacenar
     * @param code El código de registro asignado por la aplicación
     * @param message El mensaje a almacenar en registro de windows event viewer
     */
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
                generateErrorLog(AppConst.ErrorMessages.ERROR_DELETE);
            }
        }catch(Win32Exception e){
            generateErrorLog(AppConst.ErrorMessages.ERROR_DELETE + e.getMessage());
        } finally{
            advapi32.CloseEventLog(handle);
        }
    }
}
