package com.andriiginting.vids.data

import android.app.Notification
import android.content.Context
import com.andriiginting.vids.R
import com.andriiginting.vids.feeds.FeedVideo
import com.andriiginting.vids.feeds.VideoType
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.Executors

class FeedDownloadManager : DownloadService(2021, DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL) {

    private lateinit var notificationHelper: DownloadNotificationHelper
    private val context: Context = this

    override fun onCreate() {
        super.onCreate()
        notificationHelper = DownloadNotificationHelper(
            this,
            context.resources.getString(R.string.app_name)
        )
    }

    override fun getDownloadManager(): DownloadManager {
        val dataSource = DefaultHttpDataSourceFactory(
            Util.getUserAgent(context, context.resources.getString(R.string.app_name)),
            null,
            30 * 1000, 30 * 1000,
            true
        )

        return DownloadManager(
            context,
            ExoDatabaseProvider(context),
            FeedsVideoCache.getInstance(context),
            dataSource,
            Executors.newFixedThreadPool(6)
        ).apply {
            maxParallelDownloads = 3
            addListener(object : DownloadManager.Listener {
                override fun onDownloadsPausedChanged(
                    downloadManager: DownloadManager,
                    downloadsPaused: Boolean
                ) {
                    super.onDownloadsPausedChanged(downloadManager, downloadsPaused)
                    if (downloadsPaused.not()) {
                        Log.d("feeds-download", "file still downloading")
                    }
                }
            })
        }
    }

    override fun getScheduler(): Scheduler? {
        return null
    }

    override fun getForegroundNotification(downloads: MutableList<Download>): Notification {
        return notificationHelper.buildDownloadCompletedNotification(
            context,
            R.drawable.exo_notification_small_icon,
            null,
            "Downloading"
        )
    }

    companion object {
        fun downloadMediaItem(
            context: Context,
            items: VideoType.VideoItem,
            onPrepare: () -> Unit,
            onError: (e: IOException) -> Unit
        ) {
            val mediaItem = MediaItem.fromUri(items.video.url)
            val downloadHelper = DownloadHelper.forMediaItem(context, mediaItem)
            downloadHelper.prepare(object : DownloadHelper.Callback {
                override fun onPrepared(helper: DownloadHelper) {
                    Log.e("feeds-download", "onprepared download")
                    onPrepare()
                    val json = JSONObject()
                    json.put("url", items.video.url)
                    json.put("thumbnail", items.video.videoThumbnail)

                    val download = helper.getDownloadRequest(
                        items.video.url,
                        Util.getUtf8Bytes(json.toString())
                    )
                    sendAddDownload(
                        context,
                        FeedDownloadManager::class.java,
                        download,
                        true
                    )
                }

                override fun onPrepareError(helper: DownloadHelper, e: IOException) {
                    Log.e("feeds-download", "${e.message}")
                    onError(e)
                }

            })
        }

        fun getDownloadedItem(): MutableList<FeedVideo> {
            val downloadedVideo = mutableListOf<FeedVideo>()
            val cursor = FeedDownloadManager().downloadManager.downloadIndex.getDownloads()

            if (cursor.moveToFirst()) {
                do {
                    val jsonString = Util.fromUtf8Bytes(cursor.download.request.data)
                    val jsonObject = JSONObject(jsonString)
                    val uri = cursor.download.request.uri

                    downloadedVideo.add(
                        FeedVideo(
                            url = uri.toString(),
                            videoThumbnail = jsonObject.getString("thumbnail")
                        )
                    )
                } while (cursor.moveToNext())
            }

            return downloadedVideo
        }
    }

}
