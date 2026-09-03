package com.usbcommander;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.usbcommander.services.ThreadService;

public class App {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModuleConfiiguration());
        ThreadService threadService = injector.getInstance(ThreadService.class);
        threadService.start();
    }
}
