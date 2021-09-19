package com.andriiginting.vids.feeds

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andriiginting.vids.R
import com.andriiginting.vids.data.ExoPlayerProvider
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
import com.google.android.exoplayer2.source.ProgressiveMediaSource

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
                        tvDescription.text =
                            root.context.getString(R.string.feed_downloading_image_text)
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
        onVideoPlayed(player)
        binding.ivThumbnail.gone()
        binding.videoExoplayer.apply {
            this.player = player
            visible()
        }
    }

    override fun onPlayed() {
        binding.loadingLayout.root.gone()
        binding.root.postDelayed({
            if (binding.videoExoplayer.player != null) {
                binding.videoExoplayer.visible()
                binding.ivThumbnail.gone()
            }
        }, 500L)
    }

    override fun onCancelled() {
        binding.videoExoplayer.player = null
        binding.videoExoplayer.gone()
        binding.ivThumbnail.visible()
        binding.loadingLayout.root.gone()
    }

    override fun onBuffered(isBuffer: Boolean) {
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
        val mediaItem = MediaItem.fromUri(videos.video.url)
        with(player) {
            stop(true)
            clearMediaItems()
            setMediaSource(getMediaSource(mediaItem), true)
            prepare()
        }
    }

    private fun getMediaSource(mediaItem: MediaItem) = ProgressiveMediaSource.Factory(
        ExoPlayerProvider.provideCacheFactory(binding.root.context)
    ).createMediaSource(mediaItem)
}
