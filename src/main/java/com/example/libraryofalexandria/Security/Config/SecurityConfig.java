package com.example.libraryofalexandria.Security.Config;

import com.example.libraryofalexandria.Security.Jwt.JwtAuthenticationFilter;
import com.example.libraryofalexandria.Services.AdminService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@EnableMethodSecurity //Might not need
@EnableWebSecurity
public class SecurityConfig {

    private final AdminService adminService;
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int ACCOUNT_LOCKOUT_DURATION = 24 * 60 * 60; // 24 hrs

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(AdminService adminService, JwtAuthenticationFilter jwtAuthenticationFilter) {
      this.adminService = adminService;
      this.jwtAuthenticationFilter = jwtAuthenticationFilter;
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
                //Remove to create SessionID v
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/admins").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                    //Uncomment if you wanna see the h2-console v
                /* .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))*/

                //.httpBasic(Customizer.withDefaults());
                .formLogin(form -> form  // Konfigurera form-baserad inloggning
                    .loginProcessingUrl("/login")  // Sätt rätt URL för att bearbeta login
                    .defaultSuccessUrl("/home", true)  // Här anger du den URL dit användaren ska omdirigeras efter lyckad inloggning
                    .failureUrl("/login?error=true")
                    .permitAll()
                    .failureHandler(authenticationFailureHandler())
                    .successHandler(authenticationSuccessHandler())
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @Lazy // Use lazy initialization for the PasswordEncoder
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Skapa PasswordEncoder Bean
    }

    // Konfigurerar ett filter som loggar detaljer om inkommande HTTP-förfrågningar,
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setIncludeHeaders(true);
        return filter;
    }
}
