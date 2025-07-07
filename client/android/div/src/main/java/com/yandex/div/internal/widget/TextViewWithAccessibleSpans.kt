package com.yandex.div.internal.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.text.SpannableString
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.widget.ExploreByTouchHelper
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.view2.spannable.ImageSpan

internal open class TextViewWithAccessibleSpans(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): EllipsizedTextView(context, attrs, defStyleAttr) {

    private val accessibleImageSpans = mutableListOf<ImageSpan>()
    private val imageSpans = mutableListOf<ImageSpan>()
    private val spanHelper: SpanHelper?
    private var _contentDescription: String? = null

    init {
        AccessibilityStateProvider.evaluateTouchModeEnabled(context)
        if (AccessibilityStateProvider.touchModeEnabled == true) {
            spanHelper = SpanHelper()
            ViewCompat.setAccessibilityDelegate(this, spanHelper)
            accessibilityLiveRegion = ACCESSIBILITY_LIVE_REGION_POLITE
        } else {
            spanHelper = null
        }
    }

    internal fun addImageSpan(span: ImageSpan) {
        if (AccessibilityStateProvider.touchModeEnabled == true) {
            imageSpans.add(span)
            if (span.accessibility?.contentDescription != null || span.accessibility?.onClickAction != null) {
                accessibleImageSpans.add(span)
            }

            spanHelper?.invalidateVirtualView(accessibleImageSpans.size - 1)
        }
    }

    internal fun clearImageSpans() {
        accessibleImageSpans.clear()
        imageSpans.clear()
        spanHelper?.invalidateRoot()
        evaluateAndSetContentDescription()
    }

    private fun evaluateAndSetContentDescription() {
        if (AccessibilityStateProvider.touchModeEnabled != true) {
            super.setContentDescription(_contentDescription)
            return
        }

        val evaluated = when {
            imageSpans == null || _contentDescription != null -> _contentDescription
            imageSpans.size == 0 -> null
            text.isEmpty() -> null
            /* no content description, had to remove '#' from read text */ else ->
                (text as? SpannableString)?.let { spannable ->
                    val starts = imageSpans.map { spannable.getSpanStart(it) }.sortedByDescending { it }
                    val sb = StringBuilder()
                    var start = 0

                    starts.forEach {
                        sb.append(text.subSequence(start, it))
                        start = it + 1
                    }
                    sb.append(text.subSequence(start, text.length))
                    sb.toString()
                } ?: text.toString()
        }

        super.setContentDescription(evaluated)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        evaluateAndSetContentDescription()
    }

    override fun setContentDescription(contentDescription: CharSequence?) {
        _contentDescription = contentDescription?.toString()
        super.setContentDescription(contentDescription)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        accessibleImageSpans.forEachIndexed { index, _ -> spanHelper?.invalidateVirtualView(index) }
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        spanHelper?.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
    }

    override fun dispatchHoverEvent(event: MotionEvent): Boolean {
        return spanHelper?.dispatchHoverEvent(event) == true || super.dispatchHoverEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return spanHelper?.dispatchKeyEvent(event) == true || super.dispatchKeyEvent(event)
    }

    private inner class SpanHelper : ExploreByTouchHelper(this@TextViewWithAccessibleSpans) {
        override fun getVirtualViewAt(x: Float, y: Float): Int {
            val bounds = RectF()
            accessibleImageSpans.forEachIndexed { index, child ->
                child.getBoundsInText(bounds)
                    .offset(paddingLeft.toFloat(), paddingTop.toFloat())
                if (bounds.contains(x, y)) {
                    return index
                }
            }
            return HOST_ID
        }

        override fun getVisibleVirtualViews(virtualViewIds: MutableList<Int>) {
            accessibleImageSpans.forEachIndexed { index, _ ->
                virtualViewIds.add(index)
            }
        }

        override fun onPopulateNodeForVirtualView(
            virtualViewId: Int,
            node: AccessibilityNodeInfoCompat
        ) {
            val span = getSpanForId(virtualViewId) ?: return

            node.className = span.accessibility?.accessibilityType ?: ""
            node.packageName = context.packageName

            val bounds = span.getBoundsInText(Rect()).apply {
                offset(paddingLeft, paddingTop)
            }
            node.contentDescription = span.accessibility?.contentDescription

            if (span.accessibility?.onClickAction == null) {
                node.isClickable = false
            } else {
                node.isClickable = true
                node.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK)
            }
            node.setBoundsInParent(bounds)
        }

        override fun onPerformActionForVirtualView(virtualViewId: Int, action: Int, arguments: Bundle?): Boolean {
            val clickAction = getSpanForId(virtualViewId)?.accessibility?.onClickAction ?: return false

            when (action) {
                AccessibilityNodeInfoCompat.ACTION_CLICK -> clickAction.perform()
                else -> return false
            }
            return true
        }

        private fun getSpanForId(id: Int): ImageSpan? {
            return when {
                id == HOST_ID -> null
                accessibleImageSpans.size == 0 -> null
                id >= accessibleImageSpans.size -> null
                id < 0 -> null
                else -> accessibleImageSpans[id]
            }
        }
    }
}
