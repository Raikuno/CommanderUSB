package com.usbcommander.server.security;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import com.usbcommander.server.AppConst;
import com.usbcommander.server.service.IJwtService;
import com.usbcommander.server.service.ISessionService;
import com.usbcommander.server.service.IUserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de autenticación que permite la utilización de jwt en la aplicación como sistema de sesión stateless
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ISessionService sessionService;
    private final IUserService userService;
    private final boolean cookieSecure;


    public JwtAuthenticationFilter(IJwtService jwtUtils,
        UserDetailsService userDetailsService, ISessionService sessionService,
        IUserService userService, boolean cookieSecure) {
        this.jwtService = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.sessionService = sessionService;
        this.userService = userService;
        this.cookieSecure = cookieSecure;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token =  null;
        String refreshToken = null;
        String username = null;
        String authHeader = request.getHeader("Authorization");
        Boolean valid = false;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if(request.getCookies() != null){
            token = Arrays.stream(request.getCookies())
            .filter(t -> t.getName().equals(AppConst.ACCESS_TOKEN_NAME)).findFirst()
            .map(c -> c.getValue()).orElse(null);
            refreshToken = Arrays.stream(request.getCookies())
            .filter(t -> t.getName().equals(AppConst.REFRESH_TOKEN_NAME)).findFirst()
            .map(c -> c.getValue()).orElse(null);
        }

        valid = token != null && jwtService.validateToken(token);
        if(!valid){
            if(refreshToken != null 
                && jwtService.isRefreshToken(refreshToken)
                && jwtService.validateToken(refreshToken)
                && sessionService.findValidSession(refreshToken).isPresent()
            ){
                username = jwtService.getEmailFromToken(refreshToken);
                var user = userService.getByEmail(username);
                if(user.isEmpty()){
                    filterChain.doFilter(request, response);
                    return;
                }
                token = jwtService.generateAccessToken(user.get());
                response.addHeader(HttpHeaders.SET_COOKIE,
                    ResponseCookie.from(AppConst.ACCESS_TOKEN_NAME, token)
                    .httpOnly(true)
                    .path("/")
                    .secure(cookieSecure)
                    .sameSite("Strict")
                    .maxAge(jwtService.getAccessTokenSeconds())
                    .build().toString()
                );

            } else{
                filterChain.doFilter(request, response);
                return;
            }
        } else {
            username = jwtService.getEmailFromToken(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("Authenticated user " + username + ", setting security context");
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
            

        filterChain.doFilter(request, response);
    }
}
