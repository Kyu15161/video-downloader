package com.example.network

import android.content.Context
import android.webkit.CookieManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.util.concurrent.TimeUnit

object NetworkClient {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    // Inject cookies from our WebView sessions into normal OkHttp requests.
    private val cookieInterceptor = Interceptor { chain ->
        val request = chain.request()
        val urlStr = request.url.toString()
        
        // Priority 1: EncryptedSharedPreferences
        var cookies = if (::appContext.isInitialized) {
            TokenStorage.getCookies(appContext, urlStr)
        } else null
        
        // Priority 2: CookieManager fallback
        if (cookies.isNullOrEmpty()) {
             cookies = CookieManager.getInstance().getCookie(urlStr)
        }
        
        val builder = request.newBuilder()
        if (!cookies.isNullOrEmpty()) {
            builder.addHeader("Cookie", cookies)
        }
        
        // Also pretend to be a standard mobile browser to help bypass simple blocks
        builder.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 10; SM-G981B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.162 Mobile Safari/537.36")
        
        chain.proceed(builder.build())
    }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(cookieInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS) // Long timeout for large downloads
            .build()
    }
}
