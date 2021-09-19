package com.andriiginting.vids.dialog

import android.util.Log
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
        if (data.url.isNotEmpty()) {
            tempList.add(data)
            onObserveUrlField(tempList)
        }
    }

    private fun onObserveUrlField(list: MutableSet<FeedVideo>) {
        if (list.size > 1) {
            _submitButtonState.value = SubmitButtonState.Enabled
        } else {
            _submitButtonState.value = SubmitButtonState.Disabled
        }
    }

    fun postVideos(data: List<FeedVideo>) {
        _videoState.value = MainFeedState.HideEmptyScreen
        actualList.addAll(data)
        _videoState.value = MainFeedState.AddVideos(actualList)
    }
}

sealed class MainFeedState {
    object HideEmptyScreen: MainFeedState()
    object ShowDefault: MainFeedState()
    data class AddVideos(val data: List<FeedVideo>): MainFeedState()
}

sealed class SubmitButtonState {
    object Enabled : SubmitButtonState()
    object Disabled : SubmitButtonState()
}