package com.andriiginting.vids

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

fun TextView.onTextChanged(
    onTextChanged: (s: CharSequence, start: Int, before: Int, count: Int) -> Unit
): TextWatcher {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            onTextChanged(s, start, before, count)
        }
    }
    this.addTextChangedListener(watcher)
    return watcher
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

fun View.screenSize(): Point {
    val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val point = Point()
    display.getSize(point)
    return point
}

fun View.showKeyboard(){
    val imm = this.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

fun View.hideKeyboard() {
    val inputManagerMethod = this.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManagerMethod.hideSoftInputFromWindow(windowToken, 0)
}
