package com.andriiginting.vids.data

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FeedsPreloadingCache : IntentService(FeedsPreloadingCache::class.java.simpleName) {

    companion object {
        const val KEY_LIST = "KEY_LIST"
    }

    private lateinit var context: Context
    private var job: Job? = null
    private var feedsVideoUrl: ArrayList<String>? = null

    private lateinit var httpDataSourceFactory: HttpDataSource.Factory
    private lateinit var defaultDataSourceFactory: DefaultDataSourceFactory
    private lateinit var cacheDataSourceFactory: CacheDataSource
    private lateinit var simpleCache: SimpleCache

    override fun onHandleIntent(intent: Intent?) {
        context = applicationContext
        simpleCache = FeedsVideoCache.getInstance(context)
        httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        defaultDataSourceFactory = DefaultDataSourceFactory(
            this, httpDataSourceFactory
        )

        cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .createDataSource()

        if (intent != null) {
            val extras = intent.extras
            feedsVideoUrl = extras?.getStringArrayList(KEY_LIST)

            if (!feedsVideoUrl.isNullOrEmpty()) {
                preLoadingVideo(feedsVideoUrl)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun preLoadingVideo(list: ArrayList<String>?) {
        var url = ""

        if (!list.isNullOrEmpty()) {
            url = list[0]
            list.removeAt(0)
        } else {
            stopSelf()
        }

        if (list.isNullOrEmpty().not()) {
            val videoUri = Uri.parse(url)
            val dataSpec = DataSpec(videoUri)

            val progressListener =
                CacheWriter.ProgressListener { requestLength, bytesCached, _ ->
                    val downloadPercentage: Double = (bytesCached * 100.0
                        / requestLength)

                    Log.d(
                        "feeds-preload",
                        "downloadPercentage $downloadPercentage videoUri: $videoUri"
                    )
                }

            job = GlobalScope.launch(Dispatchers.IO) {
                cacheVideo(dataSpec, progressListener)
                preLoadingVideo(list)
            }
        }
    }

    private fun cacheVideo(
        dataSpec: DataSpec,
        progressListener: CacheWriter.ProgressListener
    ) {
        runCatching {
            CacheWriter(
                cacheDataSourceFactory,
                dataSpec,
                true,
                null,
                progressListener
            ).cache()
        }.onFailure {
            it.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}