package com.usbcommander.server.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usbcommander.server.AppConst;
import com.usbcommander.server.entity.User;
import com.usbcommander.server.service.IFirstStartService;
import com.usbcommander.server.service.JwtService;
import com.usbcommander.server.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private IFirstStartService loginService;

    @PostMapping("/firstuser")
    public ResponseEntity<String> getMethodName(@RequestBody Map<String, String> payload) {
        if (loginService.adminAccountCreated()) {
            return ResponseEntity.badRequest().body("Admin account already exists");
        }
        var email = payload.getOrDefault("email", "").trim();
        var password = payload.getOrDefault("password", "").trim();
        var name = payload.getOrDefault("name", "").trim();

        if(email.isBlank()){
            return ResponseEntity.badRequest().body("Email is required");
        }

        if (password.isBlank() || password.length() < 8) {
            return ResponseEntity.badRequest().body("Password is invalid");
        }

        if (name.isBlank()) {
            return ResponseEntity.badRequest().body("Name is required");
        }

        try {
            loginService.createAdminAccount(email, password, name);
            return ResponseEntity.ok("Admin account successfully created");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating admin account: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "").trim();
        String password = body.getOrDefault("password", "").trim();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email, password));

            Optional<User> op = userService.getByEmail(email);
            if (op.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            User user = op.get();
            String accessToken = jwtUtils.generateAccessToken(user);
            //String refreshToken = jwtUtils.generateRefreshToken(user);
            ResponseCookie authCookie = ResponseCookie.from(AppConst.ACCESS_TOKEN_NAME, accessToken)
                    .httpOnly(true)
                    .path("/")
                    .secure(true)
                    .sameSite("Strict")
                    .maxAge(jwtUtils.getAccessTokenSeconds())
                    .build();
            return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, authCookie.toString())
            .body(Map.of(
                    "access_token", accessToken,
                    "token_type", "bearer",
                    "expires_in", jwtUtils.getAccessTokenSeconds()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @GetMapping
    ("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie authCookie = ResponseCookie.from(AppConst.ACCESS_TOKEN_NAME, "")
                .httpOnly(true)
                .path("/")
                .secure(true)
                .sameSite("Strict")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, authCookie.toString())
        .body(Map.of(
                "access_token", "",
                "token_type", "bearer",
                "expires_in", 0));
    }
}
