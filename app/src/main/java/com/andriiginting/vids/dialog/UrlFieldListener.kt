package com.andriiginting.vids.dialog

import com.andriiginting.vids.feeds.FeedVideo

interface UrlFieldListener {
    fun removeField(position: Int)
    fun observeField(data: FeedVideo)
}