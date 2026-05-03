package com.usbcommander.server.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.usbcommander.server.service.IJwtService;
import com.usbcommander.server.service.ISessionService;
import com.usbcommander.server.service.UserService;
import com.usbcommander.server.utils.Validations;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/auth")
/**
 * En esta clase se definen los endpoints dedicados a las acciones relacionadas con la autenticación de usuario
 */
public class ApiAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private IJwtService jwtService;
    @Autowired
    private UserService userService;
    @Autowired
    private IFirstStartService loginService;
    @Autowired
    private ISessionService sessionService;
    @Value("${app.cookies.secure}")
    private boolean cookieSecure;

    @PostMapping("/firstuser")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /api/auth/change-firstuser. 
     * Permite crear un usuario administrador en caso de que no existan usuarios en la base de datos.
     * @param payload La información de la petición del usuario 
     * @return Una respuesta en función de si se ha creado o no la cuenta de usuario
     */
    public ResponseEntity<String> getMethodName(@RequestBody Map<String, String> payload) {
        if (loginService.adminAccountCreated()) {
            return ResponseEntity.badRequest().body("Admin account already exists");
        }
        var email = payload.getOrDefault("email", "").trim();
        var password = payload.getOrDefault("password", "").trim();
        var name = payload.getOrDefault("name", "").trim();

        if (name.isBlank()) {
            return ResponseEntity.badRequest().body("Name is required");
        }

        if (!Validations.isValidEmail(email)) {
            return ResponseEntity.badRequest().body(Validations.EMAIL_REQUIREMENTS);
        }

        if (!Validations.isValidPassword(password)) {
            return ResponseEntity.badRequest().body(Validations.PASSWORD_REQUIREMENTS);
        }

        try {
            loginService.createAdminAccount(email, password, name);
            return ResponseEntity.ok("Admin account successfully created");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating admin account: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /api/auth/login. 
     * Permite iniciar sesión a un usuario si este introduce correctamente las credenciales.
     * @param body La información de la petición del usuario 
     * @return Una respuesta en función de si se ha iniciado sesión o no correctammnete. En caso de que se haya iniciado sesión, se devolverán el refresh token y el authentication token generados
     */
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "").trim();
        String password = body.getOrDefault("password", "").trim();

        if (!Validations.isValidEmail(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email, password));

            Optional<User> op = userService.getByEmail(email);
            if (op.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            User user = op.get();
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            sessionService.createSession(user, refreshToken);
            ResponseCookie refreshCookie = ResponseCookie.from(AppConst.REFRESH_TOKEN_NAME, refreshToken)
            .httpOnly(true)
            .path("/")
            .secure(cookieSecure)
            .sameSite("Strict")
            .maxAge(jwtService.getRefreshTokenSeconds())
            .build();
            ResponseCookie authCookie = ResponseCookie.from(AppConst.ACCESS_TOKEN_NAME, accessToken)
                    .httpOnly(true)
                    .path("/")
                    .secure(cookieSecure)
                    .sameSite("Strict")
                    .maxAge(jwtService.getAccessTokenSeconds())
                    .build();
            return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, authCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(Map.of(
                    "access_token", accessToken,
                    "refresh_token", refreshToken,
                    "token_type", "bearer",
                    "expires_in", jwtService.getAccessTokenSeconds()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @PostMapping("/refresh")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /api/auth/refresh. 
     * Método dedicado a implementaciones de cliente del servidor que no esten basados en web. Devolverá un nuevo authentication token en base al refresh token enviado, si es que se ha enviado uno
     * @param body La información de la petición del usuario 
     * @return Una respuesta en función de si el refresh token ha sido validado o no. En caso de que el refresh token sea válido, se devolverá un nuevo access token
     */
    public ResponseEntity<?> refresh(@RequestBody(required = false) Map<String, String> body,
            HttpServletRequest request) {
        String refreshToken = null;

        if (body != null && body.containsKey("refresh_token")) {
            refreshToken = body.get("refresh_token");
        }

        if (refreshToken == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (AppConst.REFRESH_TOKEN_NAME.equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null
                || !jwtService.isRefreshToken(refreshToken)
                || !jwtService.validateToken(refreshToken)
                || sessionService.findValidSession(refreshToken).isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }

        String email = jwtService.getEmailFromToken(refreshToken);
        Optional<User> user = userService.getByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(user.get());
        ResponseCookie authCookie = ResponseCookie.from(AppConst.ACCESS_TOKEN_NAME, newAccessToken)
                .httpOnly(true)
                .path("/")
                .secure(cookieSecure)
                .sameSite("Strict")
                .maxAge(jwtService.getAccessTokenSeconds())
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                .body(Map.of(
                        "access_token", newAccessToken,
                        "token_type", "bearer",
                        "expires_in", jwtService.getAccessTokenSeconds()));
    }

    @GetMapping("/logout")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /api/auth/logout. 
     * Permite al usuario cerrar sesión, eliminando las cookies que almacenan los token e invalidando el refresh token en la base de datos.
     * @param request La informacción de la petición del usuario
     * @return Una respuesta encargada de vaciar las cookies pertinentes y devolver un access token y refresh token vacios/
     */
    public ResponseEntity<?> logout(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (AppConst.REFRESH_TOKEN_NAME.equals(cookie.getName())) {
                    sessionService.invalidateSession(cookie.getValue());
                    break;
                }
            }
        }
        ResponseCookie authCookie = ResponseCookie.from(AppConst.ACCESS_TOKEN_NAME, "")
                .httpOnly(true)
                .path("/")
                .secure(cookieSecure)
                .sameSite("Strict")
                .maxAge(0)
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from(AppConst.REFRESH_TOKEN_NAME, "")
                .httpOnly(true)
                .path("/")
                .secure(cookieSecure)
                .sameSite("Strict")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of("access_token", "", "token_type", "bearer", "expires_in", 0));
    }
}
