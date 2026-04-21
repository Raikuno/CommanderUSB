package com.usbcommander.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.usbcommander.security.CommanderUserDetailsService;

@Configuration
@PropertySource("classpath:usbcommander.properties")
public class ServerConfig {
    @Bean
    public PasswordEncoder bcrypt(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CommanderUserDetailsService();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authProvider) throws Exception {
        http.authenticationProvider(authProvider);
        http
            .authorizeHttpRequests(auth -> 
                auth
                .requestMatchers("/login").permitAll()
                .requestMatchers("/").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            );

        return http.build();
    }

}
