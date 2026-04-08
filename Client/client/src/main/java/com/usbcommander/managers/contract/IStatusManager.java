package com.usbcommander.managers.contract;

import java.util.List;

import com.usbcommander.dto.LogDTO;
import com.usbcommander.enums.LogType;

public abstract class IStatusManager {

    protected static IStatusManager instance;

    protected IStatusManager(){}

    public abstract LogDTO generateLog(LogType type);

    public abstract LogDTO generateLog(LogType type, String message);

    public abstract List<LogDTO> getHistory();

    public abstract void deleteHistory();
}
