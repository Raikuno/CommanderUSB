package com.usbcommander.managers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.usbcommander.AppConst;
import com.usbcommander.config.MachineConfig;
import com.usbcommander.dto.LogDTO;

public class StatusManager {


    public static boolean statusLog(String desc){
    LogDTO log = new LogDTO(
        UsbRegistryManager.getAccessValue(), 
        MachineConfig.getInstance().getUsbEnable(), 
        UsbRegistryManager.getConnectedDrives(), 
        LocalDateTime.now(), 
        null); 
        log.setDescription(desc);
        
        return writeLog(log);
    }

    public static boolean statusLog(){
    LogDTO log = new LogDTO(
        UsbRegistryManager.getAccessValue(), 
        MachineConfig.getInstance().getUsbEnable(), 
        UsbRegistryManager.getConnectedDrives(), 
        LocalDateTime.now(), 
        null); 
        
        //TODO create method fillDescription(log) To define description automatically
        return writeLog(log);
    }

    private static boolean writeLog(LogDTO log){
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        System.out.println(UsbRegistryManager.getConnectedDrives().toString());
        if(verifyFolder()){
            String logName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("SS-ss-mm-hh-dd-MM-yyyy")).toString();
            File localLog = new File(AppConst.FileRoutes.LOG_FOLDER_ROUTE + "\\" + logName);
            try {
                mapper.writeValue(localLog, log);
            } catch (StreamWriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (DatabindException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }

        return true;
    }

    private static boolean verifyFolder(){
        File folder = new File(AppConst.FileRoutes.LOG_FOLDER_ROUTE);
        try {
            if(!folder.exists() || !folder.isDirectory()){
                return folder.mkdirs();
            } else {
                return true;
            }
            
        }catch(SecurityException err){
            return false;
        }
        
    }
}
