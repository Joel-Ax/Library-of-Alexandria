package com.example.libraryofalexandria;

import com.example.libraryofalexandria.Models.Admin;
import com.example.libraryofalexandria.Repositories.AdminRepository;
import com.example.libraryofalexandria.Services.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FailedLoginServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void testIncreaseFailedAttempts_LockAccount() {
        Admin admin = new Admin();
        admin.setUsername("testUser");
        admin.setFailedAttempts(2);
        admin.setAccountLocked(false);

        when(adminRepository.findByUsername("testUser")).thenReturn(Optional.of(admin));

        adminService.increaseFailedAttempts("testUser");

        assertTrue(admin.isAccountLocked());
        verify(adminRepository).save(admin);
    }

    @Test
    void testResetFailedAttempts() {
        Admin admin = new Admin();
        admin.setUsername("testUser");
        admin.setFailedAttempts(2);

        when(adminRepository.findByUsername("testUser")).thenReturn(Optional.of(admin));

        adminService.resetFailedAttempts("testUser");

        assertEquals(0, admin.getFailedAttempts());
        verify(adminRepository).save(admin);
    }

    @Test
    void testUnlockAccountIfNecessary() {
        Admin admin = new Admin();
        admin.setUsername("testUser");
        admin.setAccountLocked(true);
        admin.setLockTime(System.currentTimeMillis() - 25 * 60 * 60 * 1000); // 25 timmar sedan.

        boolean unlocked = adminService.unlockAccountIfNecessary(admin);

        assertTrue(unlocked);
        assertFalse(admin.isAccountLocked());
        assertEquals(0, admin.getFailedAttempts());
        verify(adminRepository).save(admin);
    }
}
