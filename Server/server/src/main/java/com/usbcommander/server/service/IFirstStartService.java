package com.usbcommander.server.service;

public interface IFirstStartService {
    public boolean adminAccountCreated();
    public void createAdminAccount(String email, String password, String name);
}
