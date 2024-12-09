package com.example.libraryofalexandria;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordHashingTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void testPasswordHashing() {
        // Original lösenord
        String rawPassword = "mySecretPassword";

        // Hasha lösenordet
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // Verifiera att det hashade lösenordet inte är samma som originalet
        assertNotEquals(rawPassword, hashedPassword);

        // Verifiera att det hashade lösenordet stämmer med det angivna lösenordet
        assertTrue(passwordEncoder.matches(rawPassword, hashedPassword));
    }
}
