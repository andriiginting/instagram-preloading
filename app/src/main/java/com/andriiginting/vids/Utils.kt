package com.andriiginting.vids

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.ContextThemeWrapper
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

val View.displayMetrics: DisplayMetrics
    get() {
        val displayMetrics = DisplayMetrics()
        context.asActivity()?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return displayMetrics
    }

fun Context?.asActivity(): AppCompatActivity? = when {
    this == null -> null
    this is AppCompatActivity -> this
    this is ContextWrapper -> this.baseContext.asActivity()
    this is ContextThemeWrapper -> this.baseContext.asActivity()
    else -> null
}

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

