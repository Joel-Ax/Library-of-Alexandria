package com.example.libraryofalexandria;

import com.example.libraryofalexandria.Services.AdminService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    private final AdminService adminService;
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int ACCOUNT_LOCKOUT_DURATION = 24 * 60 * 60; // 24 hrs

    public SecurityConfig(AdminService adminService) {
        this.adminService = adminService;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String username = authentication.getName();
            adminService.resetFailedAttempts(username);
            response.sendRedirect("/home");
        };
    }
    @Bean
    public PasswordEncoder passwordEncoder() {    return NoOpPasswordEncoder.getInstance();}


    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            String username = request.getParameter("username");
            adminService.increaseFailedAttempts(username);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials or account locked.");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        //.requestMatchers("/api/admins").hasRole("ADMIN")
                        .anyRequest().authenticated()  // Alla andra begärningar kräver autentisering
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)  // För att stödja H2 console eller andra inbäddade sidor
                )

                .formLogin(form -> form  // Konfigurera form-baserad inloggning
                        .loginProcessingUrl("/login")  // Sätt rätt URL för att bearbeta login
                        .defaultSuccessUrl("/home", true)  // Här anger du den URL dit användaren ska omdirigeras efter lyckad inloggning
                        .failureUrl("/login?error=true")
                        .permitAll()
                        .failureHandler(authenticationFailureHandler())
                        .successHandler(authenticationSuccessHandler())
                )
        ;
        return http.build();
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
