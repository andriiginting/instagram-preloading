package com.andriiginting.vids.feeds

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andriiginting.vids.data.ExoPlayerProvider
import com.andriiginting.vids.dp
import com.andriiginting.vids.feeds.VideoType.Companion.VIDEO_FEED
import com.andriiginting.vids.screenSize
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.REPEAT_MODE_ALL
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach

@ExperimentalCoroutinesApi
class FeedsVideoRecyclerview @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val feedsPlayer = SimpleExoPlayer
        .Builder(context.applicationContext)
        .setMediaSourceFactory(
            DefaultMediaSourceFactory(ExoPlayerProvider.provideCacheFactory(context.applicationContext))
        )
        .build()
    private var videosListener: VideosListener? = null

    private var videoItemHeight = 233.dp
    private var screenHeight = 0

    init {
        setupPlayer()
    }

    private fun setupPlayer() {
        feedsPlayer.apply {
            playWhenReady = true
            repeatMode = REPEAT_MODE_ALL
            addListener(object : Player.EventListener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        videosListener?.onPlayed()
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == Player.STATE_BUFFERING) {
                        videosListener?.onBuffered(true)
                    } else {
                        videosListener?.onBuffered(false)
                    }
                }
            })
        }

        videoViewHolderChanges()
            .onEach(::playVideo)
            .launchIn((context as LifecycleOwner).lifecycleScope)

        addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {}

            override fun onChildViewDetachedFromWindow(view: View) {
                val holder = findContainingViewHolder(view)
                if (holder is VideosListener) {
                    holder.onCancelled()
                }
            }
        })
        val screenSize = screenSize()
        videoItemHeight = screenSize.x
        screenHeight = screenSize.y
    }

    fun setPlayerState(isPlaying: Boolean) {
        if (isPlaying) {
            feedsPlayer.play()
        } else {
            feedsPlayer.pause()
        }
    }

    private fun playVideo(listener: VideosListener?) {
        if (videosListener != null && videosListener == listener) {
            return
        }
        try {
            videosListener?.onCancelled()
            videosListener = listener
            videosListener?.onPreparedVideo(feedsPlayer)
        } catch (e: Exception) {
            Log.e("feeds-video", "${e.message}")
        }
    }

    private fun videoViewHolderChanges(): Flow<VideosListener?> {
        return callbackFlow {
            val listener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    trySend(dy)
                }
            }
            addOnScrollListener(listener)
            awaitClose {
                removeOnScrollListener(listener)
            }
        }
            .buffer(Channel.CONFLATED)
            .mapLatest { getTargetVideoHolder() }
            .flowOn(IO)
    }

    private fun getTargetVideoHolder(): VideosListener? {
        try {
            val position = findCurrentVideoPosition()
            if (position == NO_POSITION) {
                return null
            }
            val viewHolder = findViewHolderForAdapterPosition(position)
            return if (viewHolder is VideosListener) {
                viewHolder
            } else {
                null
            }

        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            return null
        }
    }

    private fun findCurrentVideoPosition(): Int {
        var knownPosition = NO_POSITION
        val linearLayoutManager = layoutManager as LinearLayoutManager
        val adapter = adapter as FeedsAdapter

        val firstPosition = linearLayoutManager.findFirstVisibleItemPosition()
        val lastPosition = linearLayoutManager.findLastVisibleItemPosition()
        var percentMax = 0

        for (position in firstPosition..lastPosition) {
            if (!adapter.isItemVideo(position)) continue
            val percent = getVisibleVideoHeight(position, linearLayoutManager)
            if (percentMax < percent) {
                percentMax = percent
                knownPosition = position
            }
        }
        return knownPosition
    }

    private fun getVisibleVideoHeight(
        position: Int,
        linearLayoutManager: LinearLayoutManager
    ): Int {
        val child = linearLayoutManager.findViewByPosition(position) ?: return NO_POSITION
        val location = IntArray(2)
        child.getLocationInWindow(location)

        return if (location[1] < 0) {
            location[1] + videoItemHeight
        } else {
            screenHeight - location[1]
        }
    }

    private fun FeedsAdapter.isItemVideo(position: Int): Boolean {
        return position != NO_POSITION && getItemViewType(position) == VIDEO_FEED
    }
}