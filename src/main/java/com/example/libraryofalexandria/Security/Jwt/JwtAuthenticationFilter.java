package com.example.libraryofalexandria.Security.Jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  // Dependency injection of JWT utility and UserDetailsService
  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;

  // Constructor to inject dependencies
  public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
      throws ServletException, IOException {

    // Retrieve the Authorization header from the incoming request
    final String authorizationHeader = request.getHeader("Authorization");

    logger.debug("Authorization Header: " + authorizationHeader);

    // Initialize username and JWT variables
    String username = null;
    String jwt = null;

    // Check if Authorization header exists and starts with "Bearer "
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      // Extract the JWT token (removing "Bearer " prefix)
      jwt = authorizationHeader.substring(7);

      logger.debug("Extracted JWT: " + jwt);

      // Extract username from the JWT token
      try {
        username = jwtUtil.extractUsername(jwt);
        logger.debug("Extracted Username: " + username);
      } catch (Exception e) {
        logger.error("JWT Validation Error: ", e);
      }
    }

    // Verify if username is present and no authentication is already set in the context
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      // Load user details from the database using the extracted username
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

      // Validate the JWT token against the loaded user details
      if (jwtUtil.validateToken(jwt, userDetails)) {
        // Create an authentication token with user details and authorities
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Set additional authentication details from the request
        usernamePasswordAuthenticationToken
            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set the authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      }
    }

    // Continue the filter chain
    filterChain.doFilter(request, response);
  }
}