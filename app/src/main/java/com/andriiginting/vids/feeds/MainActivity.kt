package com.andriiginting.vids.feeds

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.andriiginting.vids.data.FeedsPreloadingCache
import com.andriiginting.vids.databinding.ActivityMainBinding
import com.andriiginting.vids.dialog.AddPostDialog
import com.andriiginting.vids.dialog.MainFeedState
import com.andriiginting.vids.dialog.UrlCollectionViewModel
import com.andriiginting.vids.gone
import com.andriiginting.vids.hideKeyboard
import com.andriiginting.vids.visible
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private var bindingInst: ActivityMainBinding? = null
    private val binding get() = bindingInst!!

    private val viewModel by lazy {
        ViewModelProvider(this).get(UrlCollectionViewModel::class.java)
    }

    private val feedsAdapter by lazy { FeedsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingInst = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()

        binding.fabAddVideo.setOnClickListener {
            AddPostDialog().show(supportFragmentManager, AddPostDialog.TAG)
        }

        setupFeedsVideo()
        observeFeed()
    }

    override fun onPause() {
        super.onPause()
        binding.rvFeedVideo.setPlayerState(false)
    }

    override fun onResume() {
        super.onResume()
        binding.rvFeedVideo.setPlayerState(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        bindingInst = null
    }

    private fun setupFeedsVideo() {
        binding.rvFeedVideo.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = feedsAdapter
        }
    }

    private fun observeFeed() {
        viewModel.videoState.observe(this, { state ->
            when (state) {
                is MainFeedState.HideEmptyScreen -> {
                    binding.emptyLayout.root.gone()
                }
                is MainFeedState.AddVideos -> {
                    binding.rvFeedVideo.visible()
                    feedsAdapter.setData(state.data)
                    onPreloadingStarted(state.data)
                    binding.root.hideKeyboard()
                }
                MainFeedState.ShowDefault -> binding.emptyLayout.root.visible()
            }
        })
    }

    private fun setupToolbar() {
        supportActionBar?.setBackgroundDrawable(
            ContextCompat.getDrawable(this, android.R.color.white)
        )
    }

    private fun onPreloadingStarted(list: List<FeedVideo>) {
        Intent(this, FeedsPreloadingCache::class.java).apply {
            putStringArrayListExtra(FeedsPreloadingCache.KEY_LIST, ArrayList(list.map { it.url }))
        }.also(::startService)
    }
}