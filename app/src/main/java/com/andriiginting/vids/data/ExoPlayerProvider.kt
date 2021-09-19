package com.andriiginting.vids.data

import android.content.Context
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource

object ExoPlayerProvider {
    fun provideCacheFactory(context: Context) = CacheDataSource.Factory()
        .setCache(FeedsVideoCache.getInstance(context))
        .setUpstreamDataSourceFactory(provideHttpDataSource())
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    private fun provideHttpDataSource() = DefaultHttpDataSource.Factory()
        .setAllowCrossProtocolRedirects(true)
}