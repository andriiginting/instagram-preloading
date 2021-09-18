package com.andriiginting.vids.feeds

data class FeedVideo(
    val url: String,
    val videoThumbnail: String = ""
)

sealed class VideoType(val viewType: Int) {
    companion object {
        const val VIDEO_FEED = 17

        fun buildItems(videos: List<FeedVideo>): MutableList<VideoType> {
            return mutableListOf<VideoType>().apply {
                videos.map {
                    add(VideoItem(it))
                }
            }
        }
    }

    data class VideoItem(val video: FeedVideo) : VideoType(VIDEO_FEED)
}
