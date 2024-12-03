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
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 timmar i millisekunder.

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Transactional
    public void increaseFailedAttempts(String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        int newFailCount = admin.getFailedAttempts() + 1;
        if (newFailCount >= MAX_FAILED_ATTEMPTS) {
            lockAccount(admin);
        } else {
            updateFailedAttempts(newFailCount, username);
        }
    }

    @Transactional
    public void resetFailedAttempts(String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + username));
        admin.setFailedAttempts(0);
        adminRepository.save(admin);
    }

    @Transactional
    public void lockAccount(Admin admin) {
        admin.setAccountLocked(true);
        admin.setLockTime(System.currentTimeMillis());
        adminRepository.save(admin);
    }

    @Transactional
    public void updateFailedAttempts(int failedAttempts, String username) {
        adminRepository.updateFailedAttempts(failedAttempts, username);
    }

    public boolean unlockAccountIfNecessary(Admin admin) {
        if (admin.isAccountLocked() && (System.currentTimeMillis() - admin.getLockTime()) > LOCK_TIME_DURATION) {
            admin.setAccountLocked(false);
            admin.setFailedAttempts(0);
            admin.setLockTime(null);
            adminRepository.save(admin);
            return true;
        }
        return false;
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
