package com.usbcommander.services;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.usbcommander.AppConst;
import com.usbcommander.config.MachineConfig;
import com.usbcommander.config.contract.IMachineConfig;
import com.usbcommander.dto.LogDTO;
import com.usbcommander.enums.LogType;
import com.usbcommander.errors.NotConnectionException;
import com.usbcommander.errors.ServiceDisabledException;
import com.usbcommander.managers.StatusManager;
import com.usbcommander.managers.contract.IStatusManager;
import com.usbcommander.services.contract.CommanderService;

/**
 * Servicio encargado de controlar la creación y envio de los registros de la máquina
 */
public class LogService extends CommanderService{
    /**
     * Instancia del servicio
     */
    private static LogService instance;
    /**
     * Instancia de IStatusManager para la creación y almacenaje de registros
     */
    private IStatusManager statusManager;
    /**
     * Instancia de IMachineConfig para la obtención de las variables de configuración de la máquina
     */
    private IMachineConfig machineConfig;
    /**
     * Instancia de jackson para la serialización de registros como json
     */
    private ObjectMapper mapper;

    /**
     * Constructor encargado de inicializar las propiedades del objeto
     */
    private LogService(){
        this.statusManager = StatusManager.getInstance();
        this.machineConfig = MachineConfig.getInstance();
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Método estático usado para inicializar y obtener la instancia de este servicio
     * @return La instancia inicializada del Servicio
     */
    public static LogService getInstance(){
        if(instance == null){
            instance = new LogService();
        }
        return instance;
    }

    @Override
    public void run() {
        while(running){
            try {
                Thread.sleep(machineConfig.getLogFrecuency());
                statusManager.generateLog();
                sendLogs();
            } catch (InterruptedException | ServiceDisabledException e ) {
                statusManager.generateLog(LogType.ERROR, e.getMessage());
                running = false;
            }
        }
    }

    /**
     * Método empleado para llevar a cabo el envio de todos los registros actualmente almacenados en el cliente
     * @throws ServiceDisabledException En el caso de que al momento de llamarse el método, este servicio no este activo
     */
    public void sendLogs() throws ServiceDisabledException{
        if(!running){
            throw new ServiceDisabledException(AppConst.ErrorMessages.SERVICE_NOT_RUNNING);
        }
        List<LogDTO> history = statusManager.getHistory();
        try { 
            String message = mapper.writeValueAsString(history);
            boolean sended = ServerConnectionService.getInstance().sendMessage(message);
            if(sended){
                statusManager.deleteHistory();
            }
        } catch (ServiceDisabledException | NotConnectionException e) {
            statusManager.generateLog(LogType.ERROR, e.getMessage());
        } catch (JsonProcessingException e) {
            statusManager.generateLog(LogType.ERROR, AppConst.ErrorMessages.JACKSON_ERROR_TEXT + e.getMessage());
        }
    }
    
}
