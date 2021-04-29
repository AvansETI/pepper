package com.pepper.backend.services.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionServiceTest {

    EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        this.encryptionService = new EncryptionService();
    }

    @Test
    void encrypt_samePassword() {
        String original = "This is a test with the same password!";

        AtomicReference<String> decrypted = new AtomicReference<>("");
        assertDoesNotThrow(() -> {
            String encrypted = this.encryptionService.encrypt(original, "pepper");
            decrypted.set(this.encryptionService.decrypt(encrypted, "pepper"));
        });
        assertEquals(original, decrypted.toString());
    }

    @Test
    void encrypt_differentPassword() {
        String original = "This is a test with different passwords!";

        AtomicReference<String> decrypted = new AtomicReference<>("");
        assertThrows(GeneralSecurityException.class, () -> {
            String encrypted = this.encryptionService.encrypt(original, "pepper");
            decrypted.set(this.encryptionService.decrypt(encrypted, "p3pper"));
        });
        assertNotEquals(original, decrypted.toString());
    }

}
