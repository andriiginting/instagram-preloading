package com.andriiginting.vids.dialog.fields

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andriiginting.vids.databinding.DialogAddPostItemLayoutBinding
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
        binding.fieldComponent.bind {
            listener.observeField(it)
        }

        removeField(listener::removeField)
        showDismissIconIfEnabled()
    }

    fun getUrlValue(): FeedVideo {
        return FeedVideo(
            binding.fieldComponent.getUrlValue()
        )
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