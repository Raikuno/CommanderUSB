package com.usbcommander;

import com.usbcommander.services.ThreadService;

public class App {
    public static void main(String[] args) {

        ThreadService.getInstance().start();
    }
}
