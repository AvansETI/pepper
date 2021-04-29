package com.pepper.care.core.services.encryption

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.security.GeneralSecurityException

@RunWith(AndroidJUnit4::class)
class EncryptionServiceTest {

    val encryptionService: EncryptionService = EncryptionService()

    @Test
    fun encrypt_samePassword_success() {
        val original = "This is a test with the same password!"
        var decrypted = ""

        try {
            val encrypted = encryptionService.encrypt(original, "pepper")
            decrypted = encryptionService.decrypt(encrypted, "pepper")
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
            fail()
        }

        assertEquals(original, decrypted)
    }

    @Test
    fun encrypt_differentPassword_fails() {
        val original = "This is a test with different passwords!"
        var decrypted = ""

        try {
            val encrypted = encryptionService.encrypt(original, "pepper")
            decrypted = encryptionService.decrypt(encrypted, "p3pper")
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
            assertTrue(true)
        }

        assertNotEquals(original, decrypted)
    }

}
