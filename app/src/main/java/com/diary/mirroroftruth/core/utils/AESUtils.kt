package com.diary.mirroroftruth.core.utils

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Utility class for AES-256 encryption and decryption.
 * Primarily used to secure JSON backup exports with a user-defined password.
 */
object AESUtils {

    private fun generateKey(password: String): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = password.toByteArray(Charsets.UTF_8)
        digest.update(bytes, 0, bytes.size)
        val key = digest.digest()
        return SecretKeySpec(key, "AES")
    }

    fun encrypt(data: String, password: String): String {
        val secretKeySpec = generateKey(password)
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val encryptedValue = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedValue, Base64.DEFAULT)
    }

    fun decrypt(data: String, password: String): String {
        val secretKeySpec = generateKey(password)
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val decryptedValue = cipher.doFinal(Base64.decode(data, Base64.DEFAULT))
        return String(decryptedValue, Charsets.UTF_8)
    }
}
