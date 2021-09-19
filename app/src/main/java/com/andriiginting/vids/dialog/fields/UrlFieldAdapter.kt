package com.andriiginting.vids.dialog.fields

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andriiginting.vids.dialog.UrlFieldListener
import com.andriiginting.vids.feeds.FeedVideo

interface DialogListener{
    fun onIsHiddenAddMoreButton(isHidden: Boolean)
    fun observeField(data: FeedVideo)
}

class UrlFieldAdapter(private val listener: DialogListener) : RecyclerView.Adapter<UrlFieldViewHolder>() {

    private val list = mutableListOf<Unit>()
    private val urlData = mutableListOf<FeedVideo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrlFieldViewHolder {
        return UrlFieldViewHolder.onCreate(parent)
    }

    override fun onBindViewHolder(holder: UrlFieldViewHolder, position: Int) {
        holder.bind(object : UrlFieldListener {
            override fun removeField(position: Int) {
                list.removeAt(position)
                listener.onIsHiddenAddMoreButton(false)
                notifyDataSetChanged()
            }

            override fun observeField(data: FeedVideo) {
                listener.observeField(data)
                urlData.add(data)
            }
        })
    }

    override fun getItemCount(): Int = list.size

    fun addMore(field: Unit) {
        addAndHideButtonIfRequired(field)
        notifyItemInserted(list.size)
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun getAllUrl() = urlData

    private fun addAndHideButtonIfRequired(field: Unit) {
        if (list.size < 9) {
            list.add(field)
        } else if (list.size == 9) {
            list.add(field)
            listener.onIsHiddenAddMoreButton(true)
        }
    }

    private fun getLastEmitUrl(holder: UrlFieldViewHolder, position: Int): FeedVideo {
        val map = mutableMapOf<Int, FeedVideo>()
        val value = map[position]

        if (value != null) {
            map[position] = holder.getUrlValue()
        }

        return value!!
    }
}