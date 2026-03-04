package com.usbcommander;

import com.usbcommander.interactor.RegistryInteractor;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println(new RegistryInteractor().getAccessValue());
    }
}
