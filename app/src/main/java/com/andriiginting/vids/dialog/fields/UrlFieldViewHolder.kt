package com.andriiginting.vids.dialog.fields

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andriiginting.vids.databinding.DialogAddPostItemLayoutBinding
import com.andriiginting.vids.dialog.TextChangeListener
import com.andriiginting.vids.dialog.UrlFieldListener
import com.andriiginting.vids.feeds.FeedVideo

class UrlFieldViewHolder(
    private val binding: DialogAddPostItemLayoutBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun onCreate(parent: ViewGroup): UrlFieldViewHolder {
            return UrlFieldViewHolder(
                DialogAddPostItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    fun bind(listener: UrlFieldListener) {
        val textListener = object : TextChangeListener {
            override fun invokeText(url: String) {
                Log.d("feeds-data", "valid text")
                listener.observeField(
                    FeedVideo(
                        url,
                        "https://source.unsplash.com/random/landscape"
                    )
                )
            }

            override fun invalidText() {
                Log.d("feeds-data", "invalid text")
            }
        }
        binding.fieldComponent.apply {
            bind()
            setTextChangeListener(textListener)
        }

        removeField(listener::removeField)
        showDismissIconIfEnabled()
    }

    private fun showDismissIconIfEnabled() {
        if (adapterPosition > 0) {
            binding.fieldComponent.showDismissIcon()
        } else {
            binding.fieldComponent.hideDismissIcon()
        }
    }

    private fun removeField(position: (Int) -> Unit) {
        binding.fieldComponent.removeField {
            position(adapterPosition)
        }
    }
}