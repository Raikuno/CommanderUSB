package com.usbcommander.server.security;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import com.usbcommander.server.AppConst;
import com.usbcommander.server.service.IJwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(IJwtService jwtUtils, UserDetailsService userDetailsService) {
        this.jwtService = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token =  null;
        String authHeader = request.getHeader("Authorization");
        Boolean valid = false;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if(request.getCookies() != null){
            token = Arrays.stream(request.getCookies())
            .filter(t -> t.getName().equals(AppConst.ACCESS_TOKEN_NAME)).findFirst()
            .map(c -> c.getValue()).orElse(null);
        }

        valid = token != null && jwtService.validateToken(token);
        if(!valid){
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtService.getEmailFromToken(token);
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
