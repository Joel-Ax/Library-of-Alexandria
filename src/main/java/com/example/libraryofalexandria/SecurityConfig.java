package com.example.libraryofalexandria;

import com.example.libraryofalexandria.Services.AdminService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final AdminService adminService;

    public SecurityConfig(AdminService adminService) {
        this.adminService = adminService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configure HTTP security with authentication, authorization, and CSRF settings
        http.csrf(csrf -> csrf.disable())
                .authorizeRequests(auth -> auth
                        .requestMatchers("/**").permitAll()  // Allow access to all paths
                        .requestMatchers("/api/admins").hasRole("ADMIN")  // Restrict access to admins
                        .anyRequest().authenticated())  // Require authentication for other requests
                .formLogin(form -> form
                        .loginPage("/login")  // Specify custom login page
                        .permitAll())  // Allow all users to access the login page
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // Log access denied events
                            System.out.println("Access denied for URI: " + request.getRequestURI());
                        }))
                .httpBasic(Customizer.withDefaults())  // Enable basic HTTP authentication
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));  // Set same-origin policy for frames

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Register the CommonsRequestLoggingFilter to log HTTP requests
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);  // Log client information (IP, etc.)
        filter.setIncludeQueryString(true);  // Log query parameters
        filter.setIncludePayload(true);  // Log request body
        filter.setIncludeHeaders(true);  // Log request headers
        return filter;
    }
}
