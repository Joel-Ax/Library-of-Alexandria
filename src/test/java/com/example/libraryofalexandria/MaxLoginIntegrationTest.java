package com.example.libraryofalexandria;

import com.example.libraryofalexandria.Models.Admin;
import com.example.libraryofalexandria.Repositories.AdminRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MaxLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminRepository adminRepository;

    @BeforeEach
    void setup() {
        Admin admin = new Admin();
        admin.setUsername("testUser");
        admin.setPassword("{noop}password");
        admin.setRole("ADMIN");
        admin.setFailedAttempts(0);
        admin.setAccountLocked(false);
        adminRepository.save(admin);
    }

    @AfterEach
    void cleanup() {
        adminRepository.deleteAll();
    }


    @Test
    public void testFailedLoginAttemptsAndLockout() throws Exception {
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/login")
                            .param("username", "testUser")
                            .param("password", "wrongPassword"))
                    .andExpect(status().isUnauthorized());  // Expect 401 Unauthorized

        }

        Admin admin = adminRepository.findByUsername("testUser").orElseThrow();
        assertTrue(admin.isAccountLocked());  // Ensure the account is locked
    }


    @Test
    public void testUnlockAccountAfterLockoutDuration() throws Exception {
        Admin admin = adminRepository.findByUsername("testUser").orElseThrow();
        admin.setAccountLocked(true);
        admin.setLockTime(System.currentTimeMillis() - 25 * 60 * 60 * 1000);
        adminRepository.save(admin);

        // Förvänta en 302 omdirigering efter lyckad inloggning
        mockMvc.perform(post("/login")
                        .param("username", "testUser")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())  // Förväntar att användaren omdirigeras
                .andExpect(redirectedUrl("/home"));  // Här kan du ändra "/home" om du har en annan URL som omdirigerar användaren
    }
}

