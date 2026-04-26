package com.usbcommander.server.dto;

import java.util.List;

public class RoleFormDTO {
    private String name;
    private List<String> permissionIds;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getPermissionIds() { return permissionIds; }
    public void setPermissionIds(List<String> permissionIds) { this.permissionIds = permissionIds; }
}
