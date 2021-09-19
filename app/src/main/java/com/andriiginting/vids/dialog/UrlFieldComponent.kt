package com.andriiginting.vids.dialog

import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.andriiginting.vids.databinding.ItemAddUrlBinding
import com.andriiginting.vids.feeds.FeedVideo
import com.andriiginting.vids.gone
import com.andriiginting.vids.onTextChanged
import com.andriiginting.vids.visible
import java.util.*

class UrlFieldComponent @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle), UrlFieldViewComponent {

    private val binding = ItemAddUrlBinding.inflate(LayoutInflater.from(context), this, true)
    private lateinit var textChangeListener: (FeedVideo) -> Unit
    private val clipboard =
        binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private val uniqueId = UUID.randomUUID().toString()

    private val presenter by lazy { UrlFieldPresenter(this) }

    fun bind(listener: (FeedVideo) -> Unit) {
        textChangeListener = listener
        binding.urlLayout.tvCopyClipboard.setOnClickListener {
            presenter.onPasteFromClipboard(clipboard.primaryClip?.getItemAt(0).toString())
        }

        binding.urlLayout.etAddUrl.onTextChanged { s, _, _, _ ->
            presenter.validateUrl(s.toString())
        }
    }

    fun getUrlValue() = binding.urlLayout.etAddUrl.text.toString()

    fun showDismissIcon() {
        binding.ivClearUrl.visible()
    }

    fun hideDismissIcon() {
        binding.ivClearUrl.gone()
    }

    fun removeField(removedId: (String) -> Unit) {
        binding.ivClearUrl.setOnClickListener {
            removedId(uniqueId)
        }
    }

    override fun notifyValidUrl(url: String) {
        textChangeListener(
            FeedVideo(url, "https://source.unsplash.com/weekly?japan")
        )
    }

    override fun notifyInvalidUrl() {
        textChangeListener(
            FeedVideo("", "")
        )
    }

    override fun pasteFromClipboard(url: String) {
        binding.urlLayout.etAddUrl.setText(url)
    }

    override fun hideClipboardButton() {
        binding.urlLayout.tvCopyClipboard.gone()
    }

    override fun showClipboardButton() {
        binding.urlLayout.tvCopyClipboard.visible()
    }

}

interface UrlFieldViewComponent {
    fun notifyValidUrl(url: String)
    fun notifyInvalidUrl()
    fun pasteFromClipboard(url: String)
    fun hideClipboardButton()
    fun showClipboardButton()
}