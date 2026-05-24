package com.example.network

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.net.URI

object TokenStorage {
    private const val PREFS_NAME = "secure_cookies_prefs"

    private fun getSharedPrefs(context: Context) = EncryptedSharedPreferences.create(
        PREFS_NAME,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCookies(context: Context, url: String, cookies: String) {
        val domain = extractDomain(url) ?: return
        try {
            getSharedPrefs(context).edit().putString(domain, cookies).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCookies(context: Context, url: String): String? {
        val domain = extractDomain(url) ?: return null
        return try {
            getSharedPrefs(context).getString(domain, null)
        } catch (e: Exception) {
            null
        }
    }

    private fun extractDomain(url: String): String? {
        return try {
            val uri = URI(url)
            uri.host
        } catch (e: Exception) {
            null
        }
    }
}
