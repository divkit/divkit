package com.yandex.divkit.demo.benchmark

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.internal.util.dp
import com.yandex.divkit.demo.utils.padding
import com.yandex.divkit.demo.utils.textColor

internal class Div2FeedBenchmarkViewController(
    private val recyclerView: RecyclerView
) : BenchmarkViewController {

    private val context: Context
        get() = recyclerView.context

    private val messageAdapter = SingleViewAdapter(messageView())

    override fun showMessage(message: CharSequence) {
        recyclerView.adapter = messageAdapter.apply {
            view.text = message
        }
    }

    fun showFeed(feedAdapter: Div2FeedAdapter) {
        recyclerView.adapter = feedAdapter
    }

    fun canScrollDown(): Boolean {
        return recyclerView.canScrollVertically(SCROLL_DOWN_DIRECTION)
    }

    fun scrollFeed(scrollSpeed: Int) {
        recyclerView.scrollBy(0, scrollSpeed)
    }

    private fun messageView(): TextView {
        return AppCompatTextView(context).apply {
            gravity = Gravity.CENTER
            padding = dp(32)
            textColor = Color.DKGRAY
            textSize = 24.0f
        }
    }

    private companion object {
        private const val SCROLL_DOWN_DIRECTION = 1
    }
}
