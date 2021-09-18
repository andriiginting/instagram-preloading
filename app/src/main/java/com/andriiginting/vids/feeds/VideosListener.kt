package com.andriiginting.vids.feeds

import com.google.android.exoplayer2.SimpleExoPlayer

interface VideosListener {
    fun onPreparedVideo(player: SimpleExoPlayer)
    fun onPlayed()
    fun onCancelled()
    fun onBuffered(isBuffer: Boolean)
}