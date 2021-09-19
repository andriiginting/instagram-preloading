package com.andriiginting.vids.dialog

import android.util.Patterns

class UrlFieldPresenter(private val view: UrlFieldViewComponent) {

    fun validateUrl(url: String) {
        when {
            isUrlValid(url) && url.isNotEmpty() -> {
                view.notifyValidUrl(url)
                view.hideClipboardButton()
            }
            url.isEmpty() -> {
                view.showClipboardButton()
            }
            else -> {
                view.hideClipboardButton()
                view.notifyInvalidUrl()
            }
        }
    }

    fun onPasteFromClipboard(url: String) {
        if (url.isNotEmpty() && isUrlValid(url)) {
            view.pasteFromClipboard(url)
            view.notifyValidUrl(url)
        }
    }

    private fun isUrlValid(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }
}