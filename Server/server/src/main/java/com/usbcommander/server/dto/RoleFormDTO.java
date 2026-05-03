package com.usbcommander.server.dto;

import java.util.List;

/**
 * Clase usada únicamanete como DTO (Data transfer object) para ser enviada y recibida en comunicaciones mediante peticiones REST
 */
public class RoleFormDTO {
    private String name;
    private List<String> permissionIds;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getPermissionIds() { return permissionIds; }
    public void setPermissionIds(List<String> permissionIds) { this.permissionIds = permissionIds; }
}
