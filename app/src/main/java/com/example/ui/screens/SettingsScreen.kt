package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.Platform
import com.example.ui.theme.BentoAuthContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateToLogin: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings", 
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Text(
            text = "Connected Accounts", 
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Text(
            text = "Log into platforms below. Your session tokens will be securely saved using EncryptedSharedPreferences for future downloads.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val platforms = listOf(
            PlatformInfo(Platform.YOUTUBE, "YouTube", "https://m.youtube.com/login"),
            PlatformInfo(Platform.TIKTOK, "TikTok", "https://www.tiktok.com/login"),
            PlatformInfo(Platform.INSTAGRAM, "Instagram", "https://www.instagram.com/accounts/login/")
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(platforms.size) { index ->
                val platform = platforms[index]
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        onNavigateToLogin(platform.loginUrl)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoAuthContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Auth",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = platform.displayName, 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

data class PlatformInfo(val platform: Platform, val displayName: String, val loginUrl: String)
