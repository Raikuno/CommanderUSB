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
import com.usbcommander.server.service.ISessionService;
import com.usbcommander.server.service.IUserService;

@Configuration
@PropertySource("classpath:usbcommander.properties")
public class ServerConfig {

    @Bean
    PasswordEncoder bcrypt(){
        return new BCryptPasswordEncoder(16);
    }
    
    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(IJwtService jwtUtils, UserDetailsService userDetailsService, 
        ISessionService sessionService, IUserService userService) {
        return new JwtAuthenticationFilter(jwtUtils, userDetailsService, sessionService, userService);
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
                .requestMatchers("/api/users/permissions").hasAuthority(AppConst.Authorities.MANAGE_ROLES)
                .requestMatchers("/api/users/roles").hasAuthority(AppConst.Authorities.MANAGE_ROLES)
                .requestMatchers("/api/users/roles/**").hasAuthority(AppConst.Authorities.MANAGE_ROLES)
                .requestMatchers("/api/users").hasAuthority(AppConst.Authorities.USER_MANAGEMENT)
                .requestMatchers("/api/users/**").hasAuthority(AppConst.Authorities.USER_MANAGEMENT)
                .requestMatchers("/api/machine/getUnrevisedMachines").hasAuthority(AppConst.Authorities.VIEW_LOGS)
                .requestMatchers("/api/logs/**").hasAuthority(AppConst.Authorities.VIEW_LOGS)
                .requestMatchers("/**/error-logs").hasAuthority(AppConst.Authorities.VIEW_LOGS)
                .requestMatchers("/**/revise").hasAuthority(AppConst.Authorities.SOLVE_LOGS)
                .requestMatchers("/api/logs/revise-bulk").hasAuthority(AppConst.Authorities.SOLVE_LOGS)
                .requestMatchers("/api/machine/*").hasAuthority(AppConst.Authorities.MACHINE_MANAGEMENT)
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
                .requestMatchers("/logs/**").hasAuthority(AppConst.Authorities.VIEW_LOGS)
                .requestMatchers("/machines/**").hasAuthority(AppConst.Authorities.MACHINE_MANAGEMENT)
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
