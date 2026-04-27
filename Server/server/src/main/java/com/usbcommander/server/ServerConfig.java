package com.usbcommander.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.usbcommander.server.security.JwtAuthenticationFilter;
import com.usbcommander.server.service.IJwtService;

@Configuration
@PropertySource("classpath:usbcommander.properties")
public class ServerConfig {

    @Bean
    PasswordEncoder bcrypt(){
        return new BCryptPasswordEncoder(16);
    }
    
    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(IJwtService jwtUtils, UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtUtils, userDetailsService);
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    AuthenticationManager authenticationManager(DaoAuthenticationProvider provider) {
        return new ProviderManager(provider);
    }
    
    // API security chain
    @Bean
    @Order(1)
    SecurityFilterChain apiSecurityChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter,
            DaoAuthenticationProvider authProvider) throws Exception {
        http.securityMatcher("/api/**");
        http.authenticationProvider(authProvider);

        http
        .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/permissions").hasAuthority(AppConst.Authorities.MANAGE_ROLES)
                .requestMatchers("/api/roles").hasAuthority(AppConst.Authorities.MANAGE_ROLES)
                .requestMatchers("/api/roles/**").hasAuthority(AppConst.Authorities.MANAGE_ROLES)
                .requestMatchers("/api/users").hasAuthority(AppConst.Authorities.USER_MANAGEMENT)
                .requestMatchers("/api/users/**").hasAuthority(AppConst.Authorities.USER_MANAGEMENT)
                .requestMatchers("/**/revise").hasAuthority(AppConst.Authorities.SOLVE_LOGS)
                .requestMatchers("/api/logs/revise-bulk").hasAuthority(AppConst.Authorities.SOLVE_LOGS)
                .requestMatchers("/api/logs/**").hasAuthority(AppConst.Authorities.VIEW_LOGS)
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    
    // Web (browser) security chain
    @Bean
    @Order(2)
    SecurityFilterChain webSecurityChain(HttpSecurity http, DaoAuthenticationProvider authProvider, JwtAuthenticationFilter jwtFilter) throws Exception {
        http.authenticationProvider(authProvider);

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/session/**").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/js/public/**").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .requestMatchers("/favicon.ico").permitAll()
                .requestMatchers("/error/**").permitAll()
                .requestMatchers("/users/roles").hasAuthority(AppConst.Authorities.MANAGE_ROLES)
                .requestMatchers("/users/roles/**").hasAuthority(AppConst.Authorities.MANAGE_ROLES)
                .requestMatchers("/users/permissions").hasAuthority(AppConst.Authorities.MANAGE_ROLES)
                .requestMatchers("/users/**").hasAuthority(AppConst.Authorities.USER_MANAGEMENT)
                .requestMatchers("/log/**").hasAuthority(AppConst.Authorities.VIEW_LOGS)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/session/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/session/login")))
            .exceptionHandling(ex -> ex.accessDeniedPage("/error/noAuthority"))
            .logout(logout -> 
                logout.logoutUrl("/session/logout"))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
     
}
