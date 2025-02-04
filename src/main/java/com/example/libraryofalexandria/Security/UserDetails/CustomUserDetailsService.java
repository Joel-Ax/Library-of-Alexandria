package com.example.libraryofalexandria.Security.UserDetails;

import com.example.libraryofalexandria.Models.Admin;
import com.example.libraryofalexandria.Repositories.AdminRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final AdminRepository adminRepository;

  public CustomUserDetailsService(AdminRepository adminRepository) {
    this.adminRepository = adminRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Admin admin = adminRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Admin not found with username: " + username));

    return org.springframework.security.core.userdetails.User
        .withUsername(admin.getUsername())
        .password(admin.getPassword())
        .roles(admin.getRole())
        .build();
  }
}
