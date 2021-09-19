package com.andriiginting.vids.feeds

import android.view.ViewGroup
import android.widget.Space
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class FeedsAdapter : ListAdapter<VideoType.VideoItem, RecyclerView.ViewHolder>(FeedDiffUtilCallback()) {

    private var list = mutableListOf<VideoType.VideoItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VideoType.VIDEO_FEED -> FeedViewHolder.onCreate(parent)
            else -> EmptySpaceViewHolder(Space(parent.context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = list[position]
        when (holder) {
            is FeedViewHolder -> holder.bind(data)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    override fun getItemCount(): Int = list.size

    fun setData(data: List<FeedVideo>) {
        list.clear()
        list = VideoType.buildItems(data)
        notifyDataSetChanged()
    }
}