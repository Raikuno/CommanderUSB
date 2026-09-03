package com.usbcommander.services;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Provider;
import com.usbcommander.AppConst;
import com.usbcommander.config.contract.IMachineConfig;
import com.usbcommander.dto.LogDTO;
import com.usbcommander.errors.NotConnectionException;
import com.usbcommander.errors.ServiceDisabledException;
import com.usbcommander.errors.UnallowedOperationException;
import com.usbcommander.managers.contract.IStatusManager;
import com.usbcommander.managers.contract.IUsbMemoryManager;
import com.usbcommander.services.contract.CommanderService;

/**
 * Servicio encargado de controlar la creación y envio de los registros de la máquina
 */
@com.google.inject.Singleton
public class LogService extends CommanderService{
    /**
     * Instancia de IStatusManager para la creación y almacenaje de registros
     */
    private final IStatusManager statusManager;

    private final IUsbMemoryManager usbMemoryManager;
    /**
     * Instancia de IMachineConfig para la obtención de las variables de configuración de la máquina
     */
    private final IMachineConfig machineConfig;
    /**
     * Instancia de jackson para la serialización de registros como json
     */
    private final ObjectMapper mapper;
    private final Provider<ServerConnectionService> serverConnectionServiceProvider;

    @com.google.inject.Inject
    public LogService(IStatusManager statusManager,
                      IMachineConfig machineConfig,
                      IUsbMemoryManager usbMemoryManager,
                      Provider<ServerConnectionService> serverConnectionServiceProvider){
        this.statusManager = statusManager;
        this.machineConfig = machineConfig;
        this.usbMemoryManager = usbMemoryManager;
        this.serverConnectionServiceProvider = serverConnectionServiceProvider;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void run() {
        while(running){
            try {
                Thread.sleep(machineConfig.getLogFrecuency());
                statusManager.generateLog(usbMemoryManager.getAccessValue(), usbMemoryManager.getConnectedDrives());
                sendLogs();
            } catch (InterruptedException | ServiceDisabledException e ) {
                statusManager.generateErrorLog(e.getMessage());
                running = false;
            } catch (UnallowedOperationException e) {
                statusManager.generateErrorLog(e.getMessage());
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
            boolean sended = serverConnectionServiceProvider.get().sendMessage(message);
            if(sended){
                statusManager.deleteHistory();
            }
        } catch (ServiceDisabledException | NotConnectionException e) {
            statusManager.generateErrorLog(e.getMessage());
        } catch (JsonProcessingException e) {
            statusManager.generateErrorLog(AppConst.ErrorMessages.JACKSON_ERROR_TEXT + e.getMessage());
        }
    }
    
}
