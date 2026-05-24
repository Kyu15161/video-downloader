package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.network.NetworkClient
import com.example.data.local.AppDatabase
import com.example.data.local.DownloadStatus
import com.example.data.local.VideoEntity
import com.example.data.repository.VideoRepository
import com.example.domain.Platform
import com.example.worker.DownloadWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: VideoRepository
    private val workManager = WorkManager.getInstance(application)

    init {
        NetworkClient.init(application)
        val dao = AppDatabase.getDatabase(application).videoDao()
        repository = VideoRepository(dao)
    }

    private val _selectedPlatform = MutableStateFlow<Platform?>(null)
    val selectedPlatform: StateFlow<Platform?> = _selectedPlatform

    val allVideos: StateFlow<List<VideoEntity>> = repository.allVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setPlatformFilter(platform: Platform?) {
        _selectedPlatform.value = platform
    }

    fun startDownload(url: String, platform: Platform, title: String) {
        viewModelScope.launch {
            // First insert pending video into DB
            val entity = VideoEntity(
                title = title,
                sourceUrl = url,
                platform = platform,
                status = DownloadStatus.PENDING
            )
            val videoId = repository.insert(entity).toInt()

            // Schedule WorkManager
            val inputData = Data.Builder()
                .putInt(DownloadWorker.KEY_VIDEO_ID, videoId)
                .putString(DownloadWorker.KEY_SOURCE_URL, url)
                .putString(DownloadWorker.KEY_PLATFORM, platform.name)
                .build()

            val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(inputData)
                .build()

            workManager.enqueue(downloadRequest)
            
            // Mark as downloading
            repository.update(entity.copy(id = videoId, status = DownloadStatus.DOWNLOADING))
        }
    }
}
