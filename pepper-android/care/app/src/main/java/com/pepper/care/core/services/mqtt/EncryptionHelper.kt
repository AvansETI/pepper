package com.pepper.care.core.services.mqtt

import android.util.Base64
import android.util.Log
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

class EncryptionHelper {

    companion object{
        const val ENCRYPTION_PASSWORD = "pepper"
        private const val ENCRYPTION_ERROR_TEXT = "ERROR"
        private const val ENCRYPT_ALGO = "AES/GCM/NoPadding"
        private const val TAG_LENGTH_BIT = 128 // must be one of {128, 120, 112, 104, 96}
        private const val IV_LENGTH_BYTE = 12
        private val UTF_8: Charset = StandardCharsets.UTF_8
    }

    fun encrypt(text: String, password: String): String {
        var encodedString: String

        try {
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
            encodedString = Base64.encodeToString(cipherTextWithIv, Base64.NO_WRAP)
        } catch (e: GeneralSecurityException) {
            Log.e(EncryptionHelper::class.simpleName, e.localizedMessage!!)
            encodedString = ENCRYPTION_ERROR_TEXT
        }

        return encodedString
    }

    fun decrypt(text: String, password: String): String {
        var decodedString: String

        try {
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
            decodedString = String(plainText, UTF_8)
        } catch (e: GeneralSecurityException) {
            Log.e(EncryptionHelper::class.simpleName, e.localizedMessage!!)
            decodedString = ENCRYPTION_ERROR_TEXT
        }

        return decodedString
    }

    private fun getRandomNonce(numBytes: Int): ByteArray {
        val nonce = ByteArray(numBytes)
        SecureRandom().nextBytes(nonce)
        return nonce
    }
}