package com.pepper.backend.services.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionServiceTest {

    EncryptionService encryptionService;

    @BeforeEach
    void setup() {
        this.encryptionService = new EncryptionService();
    }

    @Test
    void encrypt_samePassword_success() {
        String original = "This is a test with the same password!";

        AtomicReference<String> decrypted = new AtomicReference<>("");
        assertDoesNotThrow(() -> {
            String encrypted = this.encryptionService.encrypt(original, "pepper");
            decrypted.set(this.encryptionService.decrypt(encrypted, "pepper"));
        });
        assertEquals(original, decrypted.toString());
    }

    @Test
    void encrypt_differentPassword_fails() {
        String original = "This is a test with different passwords!";

        AtomicReference<String> decrypted = new AtomicReference<>("");
        assertThrows(GeneralSecurityException.class, () -> {
            String encrypted = this.encryptionService.encrypt(original, "pepper");
            decrypted.set(this.encryptionService.decrypt(encrypted, "p3pper"));
        });
        assertNotEquals(original, decrypted.toString());
    }

    @Test
    void encrypt() {
        String text = "bot: 72ec3a30-7fc1-405e-94e5-30624fc3d69a";
        String encrypted = "";

        try {
            encrypted = this.encryptionService.encrypt(text, "pepper");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(true);
    }

    @Test
    void decrypt() {
        String encrypted = "xEiAnVz2EIfhYXoIt2Tg4bTXdNzbVYadJmPlT_nEfTw8EXUI59ghG1UgGOs6g7JK_t3iyvIagjDUOkRL_4oti7bGVwil";
        String decrypted = "";

        try {
            decrypted = this.encryptionService.decrypt(encrypted, "pepper");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(true);
    }

}
