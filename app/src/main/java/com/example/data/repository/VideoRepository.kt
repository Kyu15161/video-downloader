package com.example.data.repository

import com.example.data.local.VideoDao
import com.example.data.local.VideoEntity
import com.example.domain.Platform
import kotlinx.coroutines.flow.Flow

class VideoRepository(private val videoDao: VideoDao) {
    val allVideos: Flow<List<VideoEntity>> = videoDao.getAllVideos()

    fun getVideosByPlatform(platform: Platform): Flow<List<VideoEntity>> {
        return videoDao.getVideosByPlatform(platform)
    }

    suspend fun insert(video: VideoEntity): Long {
        return videoDao.insertVideo(video)
    }

    suspend fun update(video: VideoEntity) {
        videoDao.updateVideo(video)
    }

    suspend fun deleteById(id: Int) {
        videoDao.deleteVideoById(id)
    }
}
