package com.usbcommander.server.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.usbcommander.server.dto.LogDTO;
import com.usbcommander.server.entity.ErrorLog;
import com.usbcommander.server.entity.Log;
import com.usbcommander.server.entity.Machine;
import com.usbcommander.server.enums.LogType;
import com.usbcommander.server.errors.MachineDisableException;
import com.usbcommander.server.service.IErrorLogService;
import com.usbcommander.server.service.ILogService;
import com.usbcommander.server.service.IMachineService;
import com.usbcommander.server.utils.CommanderLogger;
import com.usbcommander.server.utils.WrapperMapper;

@Component
@Scope("prototype")
public class MachineTalker {
    @Autowired
    private IMachineService machineService;
    private static Map<String, MachineTalker> machines = new HashMap<>();
    @Autowired
    private WrapperMapper mapper;
    @Autowired
    private CommanderLogger logger;
    @Autowired
    private ILogService logService;
    @Autowired
    private IErrorLogService errorLogService;
    @Autowired
    ApplicationContext context;

    private Socket socket;
    private Machine machine;

    public void setSocker(Socket socket) throws MachineDisableException{
        this.socket = socket;
        String ip = socket.getInetAddress().toString();
        Optional<Machine> savedMachine = machineService.getByIp(ip);
        if(savedMachine.isPresent()){
            if(savedMachine.get().getDisable()){
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.writeLog("Error closing socket for disabled machine: "+ e.getMessage());
                }
                throw new MachineDisableException("The machine with IP "+ ip + " is disabled");
            }
           machine = savedMachine.get(); 
        } else {
            Machine newMachine = new Machine();
            newMachine.setName(ip);
            newMachine.setIp(ip);
            newMachine.setDisable(false);
            newMachine.setLogFrecuency(300000L);
            newMachine.setRegisteredDate(LocalDateTime.now());
            machineService.save(newMachine);

            machine = newMachine;
        }
        machines.put(ip, this);
    }

    public void startToListen(){
        Thread listenerThread = new Thread(() -> {
            while (!socket.isClosed()) {  
                try {
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    String message = input.readUTF();
                    List<LogDTO> log = mapper.stringToLogDTOList(message);
                    log.forEach(t -> saveLog(t));
                } catch (IOException e) {
                    try {
                        socket.close();
                    } catch (IOException ioException) {
                        logger.writeLog("Error closing socket of machine: " + ioException.getMessage());
                    }
                    machines.remove(socket.getInetAddress().toString());
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public boolean sendMessage(String message){
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(message);
            out.flush();
            return true;
        } catch (IOException e) {
            logger.writeLog(e.getMessage());
            return false;
        }
    }

    public static MachineTalker getMachineTalker(String ip){
        return machines.get(ip);
    }

    public static Map<String, MachineTalker> getMachines() {
        return machines;
    }

    private void saveLog(LogDTO log){
        if(log.getCode() == LogType.ERROR.getCode() || log.getCode() == LogType.CONNECTION.getCode()){
            ErrorLog errorLog = new ErrorLog();
            errorLog.setMachine(machine);
            errorLog.setMessage(log.getErrorMessage());
            errorLog.setRecievedDate(LocalDateTime.now());
            errorLog.setCreationDate(log.getCreationDate());
            try{
                errorLogService.save(errorLog);
            }catch(Exception e){
                logger.writeLog("Duplicate Entry: "+ e.getMessage());
            }
            

        } else {

            Log newLog = new Log();
            newLog.setLogCode(log.getCode());
            newLog.setMachine(machine);
            newLog.setUsbAllowed(log.isUsbAllowed());
            newLog.setRecievedDate(LocalDateTime.now());
            newLog.setUsbValue(log.getUsbValue());
            newLog.setCreationDate(log.getCreationDate());
            if(log.getCode() == LogType.INFO.getCode()){
                newLog.setNeedsRevission(false); 
            } else {
                newLog.setNeedsRevission(true); 
            }
            try{
                logService.save(newLog);
            }catch(Exception e){
                logger.writeLog("Duplicate Entry: "+ e.getMessage());
            }
        }
    }
}
