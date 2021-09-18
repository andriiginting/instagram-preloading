package com.andriiginting.vids.feeds

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.andriiginting.vids.R
import com.andriiginting.vids.data.FeedsVideoCache
import com.andriiginting.vids.databinding.VideoFeedsItemLayoutBinding
import com.andriiginting.vids.gone
import com.andriiginting.vids.visible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheWriter

class FeedViewHolder(
    private val binding: VideoFeedsItemLayoutBinding
) : VideosListener, RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun onCreate(parent: ViewGroup): FeedViewHolder {
            return FeedViewHolder(
                VideoFeedsItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    private lateinit var videos: VideoType.VideoItem

    private val cache by lazy { FeedsVideoCache.getInstance(binding.root.context) }

    fun bind(video: VideoType.VideoItem) {
        videos = video
        Glide.with(binding.root.context)
            .load(video.video.videoThumbnail)
            /*
            to demonstrate the lottie animation
            change to DiskCacheStrategy.ALL to get better image cache
             */
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.loadingLayout.apply {
                        root.visible()
                        tvDescription.text = root.context.getString(R.string.feed_downloading_image_text)
                    }
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.loadingLayout.root.gone()
                    return false
                }

            })
            .into(binding.ivThumbnail)
    }

    override fun onPreparedVideo(player: SimpleExoPlayer) {
        Log.d("feeds-video","onPrepared video $player")
        onVideoPlayed(player)
        binding.ivThumbnail.gone()
        binding.videoExoplayer.apply {
            this.player = player
            visible()
        }
    }

    override fun onPlayed() {
        Log.d("feeds-video","onPlayed")
        binding.loadingLayout.root.gone()
        binding.root.postDelayed({
            if (binding.videoExoplayer.player != null){
                binding.videoExoplayer.visible()
                binding.ivThumbnail.gone()
            }
        }, 500L)
    }

    override fun onCancelled() {
        Log.d("feeds-video","onCancelled")
        binding.videoExoplayer.player = null
        binding.videoExoplayer.gone()
        binding.ivThumbnail.visible()
        binding.loadingLayout.root.gone()
    }

    override fun onBuffered(isBuffer: Boolean) {
        Log.d("feeds-video","onBuffered")
        binding.ivThumbnail.gone()
        binding.videoExoplayer.gone()
        binding.loadingLayout.apply {
            root.visible()
            tvDescription.text = root.context.getString(R.string.feed_loading_video_text)
            loadingAnimation.setAnimation(R.raw.loading_video)
            loadingAnimation.playAnimation()
        }
    }

    private fun onVideoPlayed(player: SimpleExoPlayer) {
        with(player) {
            stop(true)
            clearMediaItems()
            setMediaItem(
                MediaItem.fromUri(videos.video.url)
            )
            prepare()
        }
    }
}