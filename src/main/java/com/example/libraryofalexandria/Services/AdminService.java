package com.example.libraryofalexandria.Services;

import com.example.libraryofalexandria.Exceptions.ResourceNotFoundException;
import com.example.libraryofalexandria.Models.Admin;
import com.example.libraryofalexandria.Repositories.AdminRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    // Konstruktorinjektion för både AdminRepository och PasswordEncoder
    @Autowired
    public AdminService(AdminRepository adminRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;  // Här injiceras PasswordEncoder med @Lazy
    }

    // Hämta alla administratörer
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    // Skapa admin
    public Admin createAdmin(Admin admin) {
        // Validate password
        if (!isValidPassword(admin.getPassword())) {
            throw new IllegalArgumentException("Password does not meet the required criteria");
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword())); // Hasha lösenordet
        return adminRepository.save(admin);
    }

    // Radera admin
    public void deleteAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id " + id));
        adminRepository.delete(admin);
    }

    // Implementera metoden från UserDetailsService för att autentisera admin
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(admin.getUsername())
                .password(admin.getPassword())
                .roles(admin.getRole())
                .build();
    }

    // Password validation method
    private boolean isValidPassword(String password) {
        return StringUtils.isNotBlank(password) &&
                password.length() >= 12 &&
                StringUtils.containsAny(password, "ABCDEFGHIJKLMNOPQRSTUVWXYZ") && // Uppercase
                StringUtils.containsAny(password, "abcdefghijklmnopqrstuvwxyz") && // Lowercase
                StringUtils.containsAny(password, "0123456789") && // Digit
                StringUtils.containsAny(password, "!@#$%^&*()_+[]{}|;:,.<>?/`~"); // Special chars
    }
}
