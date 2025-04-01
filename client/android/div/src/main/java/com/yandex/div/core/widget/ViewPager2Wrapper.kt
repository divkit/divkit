package com.yandex.div.core.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
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
            val adapter = viewPager.adapter as DivPagerAdapter?
            if (viewPager.orientation == value && adapter?.orientation == value) {
                return
            }

            viewPager.orientation = value
            adapter?.orientation = value

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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!isWrapContentAlongCrossAxis()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        measureChild(viewPager, widthMeasureSpec, heightMeasureSpec)

        when(orientation) {
            ViewPager2.ORIENTATION_HORIZONTAL -> {
                val maxHeight = findMaxChildDimension(LayoutManager::getDecoratedMeasuredHeight)
                super.onMeasure(widthMeasureSpec, makeExactSpec(maxHeight))
            }
            ViewPager2.ORIENTATION_VERTICAL -> {
                val maxWidth = findMaxChildDimension(LayoutManager::getDecoratedMeasuredWidth)
                super.onMeasure(makeExactSpec(maxWidth), heightMeasureSpec)
            }
        }
    }

    internal fun isWrapContentAlongCrossAxis(): Boolean =
        orientation == ViewPager2.ORIENTATION_HORIZONTAL && layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT ||
        orientation == ViewPager2.ORIENTATION_VERTICAL && layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT


    private fun findMaxChildDimension(decoratedDimensionGetter: LayoutManager.(View) -> Int): Int  {
        var maxValue = 0
        withRecyclerView {
            children.forEach { child ->
                layoutManager?.let {
                    maxValue = maxOf(maxValue, it.decoratedDimensionGetter(child))
                }
            }
        }
        return maxValue
    }
}
