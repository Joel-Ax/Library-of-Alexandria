package com.example.libraryofalexandria.Services;

import com.example.libraryofalexandria.Exceptions.ResourceNotFoundException;
import com.example.libraryofalexandria.Models.Admin;
import com.example.libraryofalexandria.Models.User;
import com.example.libraryofalexandria.Repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService implements UserDetailsService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    // Hämta alla administratörer
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    // Skapa admin
    public Admin createAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    // Radera admin
    public void deleteAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id " + id));
        adminRepository.delete(admin);

    }

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
}
