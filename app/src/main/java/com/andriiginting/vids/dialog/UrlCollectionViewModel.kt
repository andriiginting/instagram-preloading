package com.andriiginting.vids.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andriiginting.vids.data.UrlFieldResponse
import com.andriiginting.vids.feeds.FeedVideo

class UrlCollectionViewModel : ViewModel() {
    private val _state = MutableLiveData<UrlDialogState>()
    val state: LiveData<UrlDialogState>
        get() = _state

    private val _submitButtonState = MutableLiveData<SubmitButtonState>(SubmitButtonState.Disabled)
    val submitButtonState: LiveData<SubmitButtonState> get() = _submitButtonState

    private val _videoState = MutableLiveData<MainFeedState>()
    val videoState: LiveData<MainFeedState> get() = _videoState

    private val list = mutableListOf<UrlFieldResponse>()

    fun populateUrlCollection(data: UrlFieldResponse) {
        list.add(data)
        if (list.size > 1) {
            _state.value = UrlDialogState.EnableDismissIcon
        }
    }

    fun removeField(id: String) {
        val response = list.find { it.id == id }
        val index = list.indexOf(response)

        list.removeAt(index)
        _state.value = UrlDialogState.RemoveUrlField(position = index)
    }

    fun onObserveUrlField(url: String) {
        if (url.isNotEmpty() || list.size > 1) {
            _submitButtonState.value = SubmitButtonState.Enabled
        } else {
            _submitButtonState.value = SubmitButtonState.Disabled
        }
    }

    fun setVideos() {
        _videoState.value = MainFeedState.HideEmptyScreen
        _videoState.value = MainFeedState.AddVideos(dummyData())
    }


    private fun dummyData(): List<FeedVideo> {
        return listOf(
            FeedVideo(
                "https://static.klliq.com/videos/uWPJnU7z5OysYjptZkBI6T1HANjC4WdP_hd.mp4",
                "https://static.klliq.com/thumbnails/ZnFAHzGD9RQrRsBjJt2Pv3Y1vIAo11FX.png"
            ),
            FeedVideo(
                "https://static.klliq.com/videos/EJUhFO-_YQkH_Ll6tPppf2EkR794aTQQ_hd.mp4",
                "https://static.klliq.com/thumbnails/ZnFAHzGD9RQrRsBjJt2Pv3Y1vIAo11FX.png"
            ),
            FeedVideo(
                "https://static.klliq.com/videos/uWPJnU7z5OysYjptZkBI6T1HANjC4WdP_hd.mp4",
                "https://static.klliq.com/thumbnails/ZnFAHzGD9RQrRsBjJt2Pv3Y1vIAo11FX.png"
            ),
            FeedVideo(
                "https://static.klliq.com/videos/EJUhFO-_YQkH_Ll6tPppf2EkR794aTQQ_hd.mp4",
                "https://static.klliq.com/thumbnails/5a7Byj0r5ZIKC0gV9QWCneZQZEmKCP-B.png"
            ),
            FeedVideo(
                "https://static.klliq.com/videos/uWPJnU7z5OysYjptZkBI6T1HANjC4WdP_hd.mp4",
                "https://static.klliq.com/thumbnails/UFfUCqtb4FYwLRmI_m2Pq8xvRw-7vA-2.png"
            ),
            FeedVideo(
                "https://static.klliq.com/videos/EJUhFO-_YQkH_Ll6tPppf2EkR794aTQQ_hd.mp4",
                "https://static.klliq.com/thumbnails/5a7Byj0r5ZIKC0gV9QWCneZQZEmKCP-B.png"
            )
        )
    }
}

sealed class UrlDialogState {
    object EnableDismissIcon : UrlDialogState()
    data class RemoveUrlField(val position: Int) : UrlDialogState()
}

sealed class MainFeedState {
    object HideEmptyScreen: MainFeedState()
    data class AddVideos(val data: List<FeedVideo>): MainFeedState()
}

sealed class SubmitButtonState {
    object Enabled : SubmitButtonState()
    object Disabled : SubmitButtonState()
}