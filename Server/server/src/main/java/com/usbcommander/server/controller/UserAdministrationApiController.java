package com.usbcommander.server.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usbcommander.server.dto.RoleFormDTO;
import com.usbcommander.server.entity.Permission;
import com.usbcommander.server.entity.Role;
import com.usbcommander.server.entity.User;
import com.usbcommander.server.service.IPermissionService;
import com.usbcommander.server.service.IRoleService;
import com.usbcommander.server.service.IUserService;
import com.usbcommander.server.utils.Validations;

@RestController
@RequestMapping("/api/users")
public class UserAdministrationApiController {

    @Autowired 
    private IUserService userService;
    @Autowired 
    private IRoleService roleService;
    @Autowired 
    private IPermissionService permissionService;

    @GetMapping({"", "/"})
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> user) {
        if (user.get("name") == null || user.get("name").isBlank()
                || user.get("email") == null || user.get("email").isBlank()
                || user.get("password") == null || user.get("password").isBlank()) {
            return ResponseEntity.badRequest().body("Name, email and password are required");
        }
        if (!Validations.isValidEmail(user.get("email"))) {
            return ResponseEntity.badRequest().body(Validations.EMAIL_REQUIREMENTS);
        }
        if (!Validations.isValidPassword(user.get("password"))) {
            return ResponseEntity.badRequest().body(Validations.PASSWORD_REQUIREMENTS);
        }
        if (userService.getByEmail(user.get("email")).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use");
        }
        if (userService.getByName(user.get("name")).isPresent()) {
            return ResponseEntity.badRequest().body("Username already in use");
        }
        User newUser = new User();
        newUser.setName(user.get("name"));
        newUser.setEmail(user.get("email"));
        newUser.setPassword(user.get("password"));
        newUser.setDisable(false);
        if (user.get("roleId") != null && !user.get("roleId").isBlank()) {
            roleService.getById(UUID.fromString(user.get("roleId"))).ifPresent(newUser::setRole);
        }
        userService.create(newUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        try {
            return userService.getById(UUID.fromString(id))
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody Map<String, String> data) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid user ID");
        }

        Optional<User> found = userService.getById(uuid);
        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String name = data.get("name");
        String email = data.get("email");
        if (name == null || name.isBlank() || email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Name and email are required");
        }

        if (!Validations.isValidEmail(email)) {
            return ResponseEntity.badRequest().body(Validations.EMAIL_REQUIREMENTS);
        }

        Optional<User> byName = userService.getByName(name);
        if (byName.isPresent() && !byName.get().getId().equals(uuid)) {
            return ResponseEntity.badRequest().body("Username already in use");
        }

        Optional<User> byEmail = userService.getByEmail(email);
        if (byEmail.isPresent() && !byEmail.get().getId().equals(uuid)) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        User user = found.get();
        user.setName(name);
        user.setEmail(email);
        user.setRole(null);
        if (data.get("roleId") != null && !data.get("roleId").isBlank()) {
            try {
                roleService.getById(UUID.fromString(data.get("roleId"))).ifPresent(user::setRole);
            } catch (IllegalArgumentException ignored) {}
        }
        userService.update(user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/{id}/disable")
    public ResponseEntity<?> toggleDisable(@PathVariable String id) {
        return userService.getById(UUID.fromString(id))
                .map(user -> {
                    user.setDisable(!user.getDisable());
                    userService.update(user);
                    return ResponseEntity.ok(Map.of("disable", user.getDisable()));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAll());
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable String id) {
        return roleService.getById(UUID.fromString(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/roles")
    public ResponseEntity<?> createRole(@RequestBody RoleFormDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Role name is required");
        }
        if (roleService.getByName(dto.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Role name already exists");
        }
        Role role = new Role();
        role.setName(dto.getName());
        role.setPermissions(resolvePermissions(dto.getPermissionIds()));
        roleService.save(role);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<?> updateRole(@PathVariable String id, @RequestBody RoleFormDTO dto) {
        return roleService.getById(UUID.fromString(id))
                .map(role -> {
                    if (role.getName().equalsIgnoreCase("administrator")) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("The administrator role cannot be modified");
                    }
                    if (dto.getName() == null || dto.getName().isBlank()) {
                        return ResponseEntity.badRequest().body("Role name is required");
                    }
                    role.setName(dto.getName());
                    role.setPermissions(resolvePermissions(dto.getPermissionIds()));
                    roleService.save(role);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/permissions")
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAll());
    }

    private Set<Permission> resolvePermissions(List<String> ids) {
        Set<Permission> perms = new HashSet<>();
        if (ids == null) return perms;
        for (String idStr : ids) {
            try {
                permissionService.getById(UUID.fromString(idStr)).ifPresent(perms::add);
            } catch (IllegalArgumentException ignored) {}
        }
        return perms;
    }
}
