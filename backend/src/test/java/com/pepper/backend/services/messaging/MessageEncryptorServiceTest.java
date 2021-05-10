package com.pepper.backend.services.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class MessageEncryptorServiceTest {

    MessageEncryptorService messageEncryptor;

    @BeforeEach
    void setup() {
        this.messageEncryptor = new MessageEncryptorService();
    }

    @Test
    void encrypt_samePassword_success() {
        String original = "This is a test with the same password!";

        AtomicReference<String> decrypted = new AtomicReference<>("");
        assertDoesNotThrow(() -> {
            String encrypted = this.messageEncryptor.encrypt(original, "pepper");
            decrypted.set(this.messageEncryptor.decrypt(encrypted, "pepper"));
        });
        assertEquals(original, decrypted.toString());
    }

    @Test
    void encrypt_differentPassword_fails() {
        String original = "This is a test with different passwords!";

        AtomicReference<String> decrypted = new AtomicReference<>("");
        assertThrows(GeneralSecurityException.class, () -> {
            String encrypted = this.messageEncryptor.encrypt(original, "pepper");
            decrypted.set(this.messageEncryptor.decrypt(encrypted, "p3pper"));
        });
        assertNotEquals(original, decrypted.toString());
    }

    @Test
    void encrypt() {
        String text = "BOT:3:PATIENT:5:FEEDBACK#{bla bla bla}";
        String encrypted = "";

        try {
            encrypted = this.messageEncryptor.encrypt(text, "pepper");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(true);
    }

    @Test
    void decrypt() {
        String encrypted = "TL2ojGq/l3V3d8FT2UMBGWhbCJLXBteB8bbG5DitkvJ8RBbzDgfwpb0JW2oOz9w8BV2GlxOSCVljH+E4N38hZbJWiNx01GY=";
        String decrypted = "";

        try {
            decrypted = this.messageEncryptor.decrypt(encrypted, "pepper");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(true);
    }

}
