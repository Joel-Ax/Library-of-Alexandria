package com.example.libraryofalexandria.Services;

import com.example.libraryofalexandria.Models.Admin;
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
    public Admin deleteAdmin(Long id) {
        Admin adminToDelete = adminRepository.findById(id).orElse(null);
        if (adminToDelete != null) {
            adminRepository.delete(adminToDelete);
        }
        return adminToDelete;
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
