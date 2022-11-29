package com.yandex.divkit.demo.benchmark

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.util.dp
import com.yandex.divkit.demo.utils.padding
import com.yandex.divkit.demo.utils.textColor

internal class Div2BenchmarkViewController(
    private val profilingLayout: ProfilingLayout
) : BenchmarkViewController {

    private val context: Context
        get() = profilingLayout.context

    override fun showMessage(message: CharSequence) {
        profilingLayout.run {
            removeAllViews()
            addView(messageView(message), messageLayoutParams())
        }
    }

    fun showDiv(divView: Div2View) {
        profilingLayout.run {
            removeAllViews()
            addView(divView, divLayoutParams())
        }
    }

    fun onNextFrame(callback: (ProfilingLayout.FrameMetrics) -> Unit) {
        profilingLayout.addListener(object : ProfilingLayout.FrameMetricsListener {
            override fun onMetricsMeasured(layout: ProfilingLayout, metrics: ProfilingLayout.FrameMetrics) {
                callback(metrics)
                layout.removeListener(this)
            }
        })
    }

    private fun messageView(message: CharSequence): TextView {
        return AppCompatTextView(context).apply {
            gravity = Gravity.CENTER
            padding = dp(32)
            textColor = Color.DKGRAY
            textSize = 24.0f
            text = message
        }
    }

    private fun messageLayoutParams(): LayoutParams {
        return FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    private fun divLayoutParams(): LayoutParams {
        return FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
}
