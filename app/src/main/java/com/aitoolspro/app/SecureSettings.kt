package com.aitoolspro.app

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

data class ProviderConfig(
    val baseUrl: String = "https://openrouter.ai/api/v1",
    val model: String = "",
    val apiKey: String = ""
)

object SecureSettings {
    private const val PREFS = "ai_tools_pro_settings"
    private const val KEY_ALIAS = "ai_tools_pro_api_key_key"

    fun loadProvider(context: Context): ProviderConfig {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val encrypted = prefs.getString("api_key_encrypted", "") ?: ""
        return ProviderConfig(
            baseUrl = prefs.getString("base_url", "https://openrouter.ai/api/v1") ?: "https://openrouter.ai/api/v1",
            model = prefs.getString("model", "") ?: "",
            apiKey = try {
                if (encrypted.isBlank()) "" else decrypt(encrypted)
            } catch (_: Exception) {
                ""
            }
        )
    }

    fun saveProvider(context: Context, baseUrl: String, model: String, apiKey: String) {
        val normalizedUrl = baseUrl.trim().trimEnd('/')
        require(normalizedUrl.startsWith("https://", ignoreCase = true)) {
            "Gunakan alamat API HTTPS."
        }
        val encryptedKey = if (apiKey.isBlank()) "" else encrypt(apiKey.trim())
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("base_url", normalizedUrl)
            .putString("model", model.trim())
            .putString("api_key_encrypted", encryptedKey)
            .apply()
    }

    fun getLanguage(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString("language", "id") ?: "id"

    fun setLanguage(context: Context, language: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("language", if (language == "en") "en" else "id")
            .apply()
    }

    private fun secretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val existing = keyStore.getKey(KEY_ALIAS, null)
        if (existing is SecretKey) return existing

        val generator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }

    private fun encrypt(value: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        val combined = cipher.iv + encrypted
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    private fun decrypt(value: String): String {
        val combined = Base64.decode(value, Base64.NO_WRAP)
        require(combined.size > 12) { "Data kunci tidak valid." }
        val iv = combined.copyOfRange(0, 12)
        val encrypted = combined.copyOfRange(12, combined.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(128, iv))
        return cipher.doFinal(encrypted).toString(Charsets.UTF_8)
    }
}
