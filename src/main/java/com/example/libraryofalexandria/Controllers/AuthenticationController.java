package com.example.libraryofalexandria.Controllers;

import com.example.libraryofalexandria.Models.Admin;
import com.example.libraryofalexandria.Repositories.AdminRepository;
import com.example.libraryofalexandria.Security.Jwt.JwtUtil;
import com.example.libraryofalexandria.Services.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final JwtUtil jwtUtil;
  private final AdminRepository adminRepository;
  private final AdminService adminService;

  public AuthenticationController(
      AdminRepository adminRepository,
      AdminService adminService,
      AuthenticationManager authenticationManager,
      UserDetailsService userDetailsService,
      JwtUtil jwtUtil
  ) {
    this.adminRepository = adminRepository;
    this.adminService = adminService;
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.jwtUtil = jwtUtil;
  }

  // Login DTO
  public static class LoginRequest {
    private String username;
    private String password;

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
  }

  // Response DTO
  public static class AuthenticationResponse {
    private final String jwt;

    public AuthenticationResponse(String jwt) {
      this.jwt = jwt;
    }

    public String getJwt() { return jwt; }
  }

  @PostMapping("/login")
  public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest) throws Exception {
    try {

      // Hämta användaren först
      Optional<Admin> adminOptional = adminRepository.findByUsername(authenticationRequest.getUsername());

      // Om användaren finns, försök låsa upp kontot
      if (adminOptional.isPresent()) {
        Admin admin = adminOptional.get();
        adminService.unlockAccountIfNecessary(admin);
      }

      // Kontrollera om kontot är låst
      if (adminOptional.isPresent() && adminOptional.get().isAccountLocked()) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is locked");
      }
      // Authenticate the user
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authenticationRequest.getUsername(),
              authenticationRequest.getPassword()
          )
      );

      // Om autentiseringen lyckas, återställ misslyckade inloggningsförsök
      adminService.resetFailedAttempts(authenticationRequest.getUsername());
    } catch (Exception e) {
      // Om autentiseringen misslyckas, öka antalet misslyckade försök
      adminService.increaseFailedAttempts(authenticationRequest.getUsername());
      return ResponseEntity.badRequest().body("Invalid credentials");
    }

    // Load user details
    final UserDetails userDetails = userDetailsService
        .loadUserByUsername(authenticationRequest.getUsername());

    // Generate JWT
    final String jwt = jwtUtil.generateToken(userDetails);

    // Return JWT
    return ResponseEntity.ok(new AuthenticationResponse(jwt));
  }
}