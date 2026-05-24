package com.example.network

import com.example.domain.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URI

object UrlAnalyzer {
    fun detectPlatform(url: String): Platform? {
        return try {
            val uri = URI(url)
            val host = uri.host?.lowercase() ?: return null
            when {
                host.contains("youtube.com") || host.contains("youtu.be") -> Platform.YOUTUBE
                host.contains("tiktok.com") -> Platform.TIKTOK
                host.contains("instagram.com") -> Platform.INSTAGRAM
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchMetadata(url: String): VideoMetadata? = withContext(Dispatchers.IO) {
        try {
            val document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .timeout(10000)
                .get()
            
            var title = document.select("meta[property=og:title]").attr("content")
            if (title.isNullOrEmpty()) {
                title = document.title()
            }
            
            val platform = detectPlatform(url) ?: Platform.YOUTUBE
            
            if (title.isNotEmpty()) {
                VideoMetadata(title = title, platform = platform)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

data class VideoMetadata(val title: String, val platform: Platform)
