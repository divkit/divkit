package com.yandex.div.core.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

internal open class ViewPager2Wrapper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    open val viewPager: ViewPager2 = ViewPager2(context).apply {
        descendantFocusability = FOCUS_BLOCK_DESCENDANTS
    }

    init {
        addView(viewPager)
    }

    final override fun addView(child: View?) {
        super.addView(child)
    }

    fun setRecycledViewPool(viewPool: RecyclerView.RecycledViewPool) {
        withRecyclerView {
            setRecycledViewPool(viewPool)
        }
    }

    private fun withRecyclerView(block: RecyclerView.() -> Unit) {
        val recyclerView: RecyclerView = viewPager.getChildAt(0) as? RecyclerView?
            ?: return // never returns as recyclerView created in ViewPager2 constructor.
        block.invoke(recyclerView)
    }

    /**
     * Orientation change via [ViewPager2.setOrientation] is broken:
     * after orientation change layout manager set each item's translation properly,
     * but translation dimensions from old orientation will not be reset to defaults.
     */
    var orientation: Int
        set(value) {
            if (viewPager.orientation == value) {
                return
            }

            viewPager.orientation = value

            withRecyclerView {
                // Sadly we need to clear pool cause it may
                // contain elements with outdated translations.
                recycledViewPool.clear()
                children.forEach { v ->
                    v.translationX = 0f
                    v.translationY = 0f
                }
            }
        }
        get() = viewPager.orientation
}
