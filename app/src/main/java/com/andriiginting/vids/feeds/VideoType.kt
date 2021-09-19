package com.andriiginting.vids.feeds

data class FeedVideo(
    val url: String,
    val videoThumbnail: String = ""
)

sealed class VideoType(val viewType: Int) {
    companion object {
        const val VIDEO_FEED = 17

        fun buildItems(videos: List<FeedVideo>): MutableList<VideoItem> {
            val list = mutableListOf<VideoItem>()
            videos.forEach { feeds ->
                list.add(VideoItem(feeds))
            }
            return list
        }
    }

    data class VideoItem(val video: FeedVideo) : VideoType(VIDEO_FEED)
}
