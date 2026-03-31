package com.usbcommander.managers.contract;

import java.util.List;
import java.util.Optional;

import com.usbcommander.dto.LogDTO;

public interface IStatusManager {
    public Optional<LogDTO> errorLog(String message);

    public Optional<LogDTO> infoLog();

    public Optional<LogDTO> incoherentValueLog();

    public Optional<LogDTO> registryModificationLog();

    public Optional<LogDTO> unauthorizedConfigurationModificationLog();

    public List<LogDTO> getHistory();

    public void deleteHistory();
}
