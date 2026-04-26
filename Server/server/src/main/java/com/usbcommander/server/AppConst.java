package com.usbcommander.server;

public class AppConst {
    public static final String REFRESH_TOKEN_NAME = "refresh_token";
    public static final String ACCESS_TOKEN_NAME = "access_token";
    public static class Authorities {
        public static final String USER_MANAGEMENT = "USER_MANAGEMENT";
        public static final String SOLVE_LOGS = "SOLVE_LOGS";
        public static final String VIEW_LOGS = "VIEW_LOGS";
        public static final String MANAGE_ROLES = "MANAGE_ROLES";
        public static final String MACHINE_MANAGEMENT = "MACHINE_MANAGEMENT";
    }
}
