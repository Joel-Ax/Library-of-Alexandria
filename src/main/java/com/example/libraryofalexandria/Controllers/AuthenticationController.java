package com.example.libraryofalexandria.Controllers;

import com.example.libraryofalexandria.Security.Jwt.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final JwtUtil jwtUtil;

  public AuthenticationController(
      AuthenticationManager authenticationManager,
      UserDetailsService userDetailsService,
      JwtUtil jwtUtil
  ) {
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
      // Authenticate the user
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authenticationRequest.getUsername(),
              authenticationRequest.getPassword()
          )
      );
    } catch (Exception e) {
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