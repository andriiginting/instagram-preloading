package com.andriiginting.vids.data

import android.content.Context
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File

object FeedsVideoCache {
    private var simpleCache: SimpleCache? = null
    private const val MAX_CACHE_SIZE = 100 * 1024 * 1024

    fun getInstance(context: Context): SimpleCache {
        val lruCache = LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE.toLong())
        val databaseProvider = ExoDatabaseProvider(context)
        if (simpleCache == null) {
            simpleCache = SimpleCache(
                File(context.cacheDir, "feeds"),
                lruCache,
                databaseProvider
            )
        }

        return simpleCache!!
    }
}