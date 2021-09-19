package com.andriiginting.vids.feeds

import androidx.recyclerview.widget.DiffUtil

class FeedDiffUtilCallback : DiffUtil.ItemCallback<VideoType.VideoItem>() {
    override fun areContentsTheSame(
        oldItem: VideoType.VideoItem,
        newItem: VideoType.VideoItem
    ): Boolean {
        return oldItem.video.url == newItem.video.url
    }

    override fun areItemsTheSame(
        oldItem: VideoType.VideoItem,
        newItem: VideoType.VideoItem
    ): Boolean {
        return oldItem.video.url.hashCode() == newItem.video.url.hashCode()
    }

}