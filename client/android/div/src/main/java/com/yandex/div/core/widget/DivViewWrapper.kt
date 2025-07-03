package com.yandex.div.core.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams
import com.yandex.div.core.util.makeFocusable
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.divs.widgets.DivBorderDrawer
import com.yandex.div.core.view2.divs.widgets.DivBorderSupports
import com.yandex.div.internal.widget.FrameContainerLayout
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.internal.widget.TransientViewMixin
import com.yandex.div2.DivBorder

/**
 * ViewGroup that mimics its child view dimensions.
 * NOTE: there is a limited support of [LayoutParams] forwarding, so to reduce unwanted effects follow next steps:
 * - first, add [DivViewWrapper] to view hierarchy
 * - next, add child view to it.
 */
internal open class DivViewWrapper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameContainerLayout(context, attrs, defStyleAttr), DivBorderSupports, TransientView by TransientViewMixin() {

    val child: View?
        get() = if (childCount == 0) null else getChildAt(0)

    init {
        makeFocusable()
        importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
    }

    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        require(childCount == 0) { "ViewWrapper can host only one child view" }
        super.addView(child, 0, params)
    }

    override var needClipping
        get() = (child as? DivBorderSupports)?.needClipping ?: true
        set(value) {
            (child as? DivBorderSupports)?.needClipping = value
        }

    override fun getDivBorderDrawer(): DivBorderDrawer? = (child as? DivBorderSupports)?.getDivBorderDrawer()

    override fun setBorder(bindingContext: BindingContext, border: DivBorder?, view: View) {
        (child as? DivBorderSupports)?.setBorder(bindingContext, border, view)
    }
}
