package com.andriiginting.vids.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andriiginting.vids.feeds.FeedVideo

class UrlCollectionViewModel : ViewModel() {

    private val _submitButtonState = MutableLiveData<SubmitButtonState>(SubmitButtonState.Disabled)
    val submitButtonState: LiveData<SubmitButtonState> get() = _submitButtonState

    private val _videoState = MutableLiveData<MainFeedState>(MainFeedState.ShowDefault)
    val videoState: LiveData<MainFeedState> get() = _videoState

    private val tempList = mutableSetOf<FeedVideo>()
    private val actualList = mutableListOf<FeedVideo>()

    fun populateUrlCollection(data: FeedVideo) {
        tempList.add(data)
        onObserveUrlField(tempList)
    }

    private fun onObserveUrlField(list: MutableSet<FeedVideo>) {
        if (list.size > 1 || list.first().url.isNotEmpty()) {
            _submitButtonState.value = SubmitButtonState.Enabled
        } else {
            _submitButtonState.value = SubmitButtonState.Disabled
        }
    }

    fun postVideos(data: List<FeedVideo>) {
        _videoState.value = MainFeedState.HideEmptyScreen
        tempList.clear()
        actualList.addAll(data)
        _videoState.value = MainFeedState.AddVideos(actualList)
    }

    //for testing purpose
    private fun provideDummy(): List<FeedVideo> {
        return listOf(
            FeedVideo(
                "https://static.klliq.com/videos/uWPJnU7z5OysYjptZkBI6T1HANjC4WdP_hd.mp4",
                "https://static.klliq.com/thumbnails/UFfUCqtb4FYwLRmI_m2Pq8xvRw-7vA-2.png"
            ),
            FeedVideo(
                "https://static.klliq.com/videos/0HkyPAfPcmN0r5WxkYYvIHSi9jcC8Z_I_hd.mp4",
                "https://static.klliq.com/thumbnails/uYSHHSfB6F183ZHYk1OnBjCe5C_1yseF.png"
            ),
            FeedVideo(
                "https://static.klliq.com/videos/EJUhFO-_YQkH_Ll6tPppf2EkR794aTQQ_hd.mp4",
                "https://static.klliq.com/thumbnails/5a7Byj0r5ZIKC0gV9QWCneZQZEmKCP-B.png"
            )
        )
    }
}

sealed class MainFeedState {
    object HideEmptyScreen : MainFeedState()
    object ShowDefault : MainFeedState()
    data class AddVideos(val data: List<FeedVideo>) : MainFeedState()
}

sealed class SubmitButtonState {
    object Enabled : SubmitButtonState()
    object Disabled : SubmitButtonState()
}