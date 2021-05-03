package com.pepper.care.core.services.encryption

import android.util.Base64
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptionService {

    private val ENCRYPT_ALGO = "AES/GCM/NoPadding"
    private val TAG_LENGTH_BIT = 128 // must be one of {128, 120, 112, 104, 96}
    private val IV_LENGTH_BYTE = 12
    private val UTF_8: Charset = StandardCharsets.UTF_8

    @Throws(GeneralSecurityException::class)
    fun encrypt(text: String, password: String): String {
        val iv = getRandomNonce(IV_LENGTH_BYTE)
        val keyDigest: MessageDigest = MessageDigest.getInstance("SHA-256")
        val keyHash: ByteArray = keyDigest.digest(password.toByteArray(UTF_8))
        val secretKey: SecretKey = SecretKeySpec(keyHash, "AES")
        val cipher: Cipher = Cipher.getInstance(ENCRYPT_ALGO)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(TAG_LENGTH_BIT, iv))
        val cipherText: ByteArray = cipher.doFinal(text.toByteArray(UTF_8))
        val cipherTextWithIv: ByteArray = ByteBuffer.allocate(iv.size + cipherText.size)
            .put(iv)
            .put(cipherText)
            .array()

        return Base64.encodeToString(cipherTextWithIv, Base64.NO_WRAP)
    }

    @Throws(GeneralSecurityException::class)
    fun decrypt(text: String, password: String): String {
        val decode: ByteArray = Base64.decode(text.toByteArray(UTF_8), Base64.NO_WRAP)
        val bb: ByteBuffer = ByteBuffer.wrap(decode)
        val iv = ByteArray(IV_LENGTH_BYTE)
        bb.get(iv)
        val cipherText = ByteArray(bb.remaining())
        bb.get(cipherText)
        val keyDigest: MessageDigest = MessageDigest.getInstance("SHA-256")
        val keyHash: ByteArray = keyDigest.digest(password.toByteArray(UTF_8))
        val secretKey: SecretKey = SecretKeySpec(keyHash, "AES")
        val cipher: Cipher = Cipher.getInstance(ENCRYPT_ALGO)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(TAG_LENGTH_BIT, iv))
        val plainText: ByteArray = cipher.doFinal(cipherText)

        return String(plainText, UTF_8)
    }

    fun getRandomNonce(numBytes: Int): ByteArray {
        val nonce = ByteArray(numBytes)
        SecureRandom().nextBytes(nonce)
        return nonce
    }
}
