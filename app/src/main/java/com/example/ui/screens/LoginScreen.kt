package com.example.ui.screens

import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.network.TokenStorage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(platformUrl: String, onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Authenticate") },
                navigationIcon = {
                    Button(onClick = onNavigateBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { padding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            url?.let {
                                val cookies = CookieManager.getInstance().getCookie(it)
                                if (!cookies.isNullOrEmpty()) {
                                    TokenStorage.saveCookies(context, it, cookies)
                                }
                            }
                        }
                    }
                    webChromeClient = WebChromeClient()
                    
                    // Essential for extracting cookies
                    CookieManager.getInstance().setAcceptCookie(true)
                    CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                    
                    loadUrl(platformUrl)
                }
            }
        )
    }
}
