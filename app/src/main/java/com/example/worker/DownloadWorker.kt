package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.data.local.DownloadStatus
import com.example.domain.Platform
import com.example.network.NetworkClient
import com.example.network.PlatformDownloaderFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class DownloadWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val videoId = inputData.getInt(KEY_VIDEO_ID, -1)
        val sourceUrl = inputData.getString(KEY_SOURCE_URL) ?: return Result.failure()
        val platformStr = inputData.getString(KEY_PLATFORM) ?: return Result.failure()
        
        val platform = Platform.valueOf(platformStr)
        val dao = AppDatabase.getDatabase(applicationContext).videoDao()
        
        return withContext(Dispatchers.IO) {
            try {
                // 1. Extract direct stream URL
                val downloader = PlatformDownloaderFactory().getDownloader(platform)
                val directUrl = downloader.extractDirectMediaUrl(sourceUrl)
                
                // 2. Setup download file
                val fileName = "video_${System.currentTimeMillis()}.mp4"
                val outputFile = File(applicationContext.getExternalFilesDir(null), fileName)
                
                // 3. Download via OkHttp (which auto-injects WebView cookies)
                val request = Request.Builder().url(directUrl).build()
                val response = NetworkClient.okHttpClient.newCall(request).execute()
                
                if (!response.isSuccessful) {
                    throw IllegalStateException("Unexpected code $response")
                }
                
                // 4. Write stream to disk
                val inputStream = response.body?.byteStream()
                val outputStream = FileOutputStream(outputFile)
                
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                
                // 5. Update DB
                val video = dao.getVideoById(videoId)
                if (video != null) {
                    dao.updateVideo(video.copy(status = DownloadStatus.COMPLETED, localFilePath = outputFile.absolutePath))
                }
                
                Result.success()
            } catch (e: Exception) {
                Log.e("DownloadWorker", "Error downloading video", e)
                Result.failure()
            }
        }
    }

    companion object {
        const val KEY_VIDEO_ID = "video_id"
        const val KEY_SOURCE_URL = "source_url"
        const val KEY_PLATFORM = "platform"
    }
}
