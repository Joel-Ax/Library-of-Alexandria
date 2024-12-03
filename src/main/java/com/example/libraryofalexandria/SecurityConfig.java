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
            adminService.resetFailedAttempts(username);  // Reset failed attempts on successful login
            response.sendRedirect("/home");  // Redirect to the home page or wherever after success
        };
    }


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
                        .requestMatchers("/**").permitAll()  // Tillåt alla för alla resurser
                        .requestMatchers("/api/admins").hasRole("ADMIN")  // Endast admin åtkomst för /api/admins
                        .anyRequest().authenticated()  // Alla andra begärningar kräver autentisering
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)  // För att stödja H2 console eller andra inbäddade sidor
                )
                .httpBasic(Customizer.withDefaults())  // Aktiverar grundläggande HTTP-inloggning
                .formLogin(form -> form  // Konfigurera form-baserad inloggning
                        .loginPage("/login")  // Använd en anpassad inloggningssida (valfritt)
                        .loginProcessingUrl("/login")  // Sätt rätt URL för att bearbeta login
                        .defaultSuccessUrl("/home", true)  // Här anger du den URL dit användaren ska omdirigeras efter lyckad inloggning
                        .failureUrl("/login?error=true")
                        .permitAll()  // Tillåt alla att komma åt inloggningssidan
                        .failureHandler(authenticationFailureHandler())  // Use your custom failure handler
                        .successHandler(authenticationSuccessHandler())  // Use your custom success handler
                )
                .logout(logout -> logout  // Konfigurera utloggning
                        .logoutUrl("/logout")  // Definiera URL för utloggning (standard är /logout)
                        .logoutSuccessUrl("/")  // Omdirigera till en specifik URL efter utloggning
                        .permitAll()  // Tillåt alla att logga ut
                );
        return http.build();
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
