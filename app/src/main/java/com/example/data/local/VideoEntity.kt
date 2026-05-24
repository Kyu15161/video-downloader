package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.Platform

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val sourceUrl: String,
    val platform: Platform,
    val localFilePath: String? = null,
    val status: DownloadStatus = DownloadStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)

enum class DownloadStatus {
    PENDING, DOWNLOADING, COMPLETED, FAILED
}
