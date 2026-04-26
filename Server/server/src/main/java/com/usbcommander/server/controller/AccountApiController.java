package com.usbcommander.server.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usbcommander.server.entity.User;
import com.usbcommander.server.security.CommanderUserDetails;
import com.usbcommander.server.service.IUserService;

@RestController
@RequestMapping("/api/account")
public class AccountApiController {

    @Autowired private IUserService userService;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal CommanderUserDetails userDetails,
            @RequestBody Map<String, String> data) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String currentPassword = data.get("currentPassword");
        String newPassword = data.get("newPassword");
        String confirmPassword = data.get("confirmPassword");

        if(newPassword.length() < 8){
            return ResponseEntity.badRequest().body("New password is invalid");
        }

        if (currentPassword == null || currentPassword.isBlank()
                || newPassword == null || newPassword.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()) {
            return ResponseEntity.badRequest().body("All fields are required");
        }

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("New passwords do not match");
        }

        User user = userService.getById(userDetails.getUser().getId()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body("Current password is incorrect");
        }

        userService.updatePassword(user, newPassword);
        return ResponseEntity.ok().build();
    }
}
