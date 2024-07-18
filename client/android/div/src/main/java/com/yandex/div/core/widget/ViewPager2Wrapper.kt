package com.yandex.div.core.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.view2.divs.pager.DivPagerAdapter
import com.yandex.div.core.view2.divs.pager.DivPagerPageTransformer

internal open class ViewPager2Wrapper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    open val viewPager: ViewPager2 = ViewPager2(context)
    internal var pageTransformer: DivPagerPageTransformer? = null
        set(value) {
            field = value
            viewPager.setPageTransformer(value)
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

    fun getRecyclerView(): RecyclerView? {
        return viewPager.getChildAt(0) as? RecyclerView // never returns as recyclerView created in ViewPager2 constructor.
    }

    private fun withRecyclerView(block: RecyclerView.() -> Unit) {
        val recyclerView = getRecyclerView() ?: return
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
            (viewPager.adapter as DivPagerAdapter?)?.orientation = value

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
