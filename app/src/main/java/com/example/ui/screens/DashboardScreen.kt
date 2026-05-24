package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.DownloadStatus
import com.example.data.local.VideoEntity
import com.example.domain.Platform
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.theme.*

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val videos by viewModel.allVideos.collectAsState()
    val selectedPlatform by viewModel.selectedPlatform.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        PlatformFilters(
            selectedPlatform = selectedPlatform,
            onPlatformSelected = { viewModel.setPlatformFilter(it) }
        )

        val filteredVideos = if (selectedPlatform == null) {
            videos
        } else {
            videos.filter { it.platform == selectedPlatform }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredVideos) { video ->
                VideoCard(video = video)
            }
        }
    }
}

@Composable
fun PlatformFilters(selectedPlatform: Platform?, onPlatformSelected: (Platform?) -> Unit) {
    ScrollableTabRow(
        selectedTabIndex = selectedPlatform?.ordinal?.plus(1) ?: 0,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        divider = {}
    ) {
        Tab(
            selected = selectedPlatform == null,
            onClick = { onPlatformSelected(null) },
            text = { Text("All", fontWeight = FontWeight.Bold) }
        )
        Platform.values().forEach { platform ->
            Tab(
                selected = selectedPlatform == platform,
                onClick = { onPlatformSelected(platform) },
                text = { Text(platform.name, fontWeight = FontWeight.Bold) }
            )
        }
    }
}

@Composable
fun VideoCard(video: VideoEntity) {
    val containerColor = when (video.status) {
        DownloadStatus.DOWNLOADING -> BentoErrorContainer
        DownloadStatus.COMPLETED -> BentoRecentContainer
        else -> BentoTotalContainer
    }
    
    val borderColor = when (video.status) {
        DownloadStatus.DOWNLOADING -> BentoErrorBorder
        DownloadStatus.COMPLETED -> BentoStorageBorder
        else -> BentoOutline
    }
    
    val contentColor = when (video.status) {
        DownloadStatus.DOWNLOADING -> Color(0xFF410002)
        DownloadStatus.COMPLETED -> BentoStorageText
        else -> Color.Black
    }

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val badgeColor = if (video.status == DownloadStatus.DOWNLOADING) BentoError else Color(0xFF004A77)
                    Badge(
                        containerColor = badgeColor,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = video.status.name,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        lineHeight = MaterialTheme.typography.titleMedium.lineHeight
                    )
                }
                
                if (video.status == DownloadStatus.DOWNLOADING) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(start = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = BentoError,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Platform: ${video.platform.name}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor.copy(alpha = 0.8f)
                )
                
                if (video.status == DownloadStatus.COMPLETED) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Done",
                        tint = Color(0xFF004A77),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
