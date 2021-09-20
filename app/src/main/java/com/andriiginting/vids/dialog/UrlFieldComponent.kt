package com.andriiginting.vids.dialog

import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import com.andriiginting.vids.databinding.ItemAddUrlBinding
import com.andriiginting.vids.gone
import com.andriiginting.vids.hideKeyboard
import com.andriiginting.vids.onTextChanged
import com.andriiginting.vids.visible

class UrlFieldComponent @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle), UrlFieldViewComponent {

    private val binding = ItemAddUrlBinding.inflate(LayoutInflater.from(context), this, true)
    private lateinit var textChangeListener: TextChangeListener

    private val presenter by lazy { UrlFieldPresenter(this) }

    fun bind() {
        binding.urlLayout.tvCopyClipboard.setOnClickListener {
            val clipboard =
                it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            presenter.onPasteFromClipboard("${clipboard.primaryClip?.getItemAt(0)?.text}")
        }

        binding.urlLayout.etAddUrl.apply {
            onTextChanged { s, _, _, _ ->
                presenter.validateUrl(s.toString())
            }
            requestFocus()
            setOnEditorActionListener { view, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    view.hideKeyboard()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }
    }

    fun setTextChangeListener(listener: TextChangeListener) {
        textChangeListener = listener
    }

    fun showDismissIcon() {
        binding.ivClearUrl.visible()
    }

    fun hideDismissIcon() {
        binding.ivClearUrl.gone()
    }

    fun removeField(removedId: () -> Unit) {
        binding.ivClearUrl.setOnClickListener {
            removedId()
        }
    }

    override fun notifyValidUrl(url: String) {
        textChangeListener.invokeText(url)
    }

    override fun notifyInvalidUrl() {
        textChangeListener.invalidText()
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

interface TextChangeListener {
    fun invokeText(url: String)
    fun invalidText()
}