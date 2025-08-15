package com.yandex.div.core.view2.animations

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.yandex.div.core.view2.animations.ViewComparator.structureEquals
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ViewComparatorTest {
    private val context = RuntimeEnvironment.application

    @Test
    fun `test structure equals`() {
        val view1 = createView1()
        val view2 = createView1()

        Assert.assertTrue(view1.structureEquals(view2))
    }

    @Test
    fun `test structure not equals child count`() {
        val view1 = createView1()
        val view2 = createView2()

        Assert.assertFalse(view1.structureEquals(view2))
    }

    @Test
    fun `test structure not equals type mismatch`() {
        val view1 = createView1()
        val view3 = createView3()

        Assert.assertFalse(view1.structureEquals(view3))
    }

    private fun createView1() = FrameLayout(context).addViews(
        LinearLayout(context).addViews(
            View(context),
            FrameLayout(context),
            RelativeLayout(context).addViews(
                View(context)
            )
        ),
        View(context)
    )

    private fun createView2() = FrameLayout(context).addViews(
        LinearLayout(context).addViews(
            View(context),
            FrameLayout(context),
            RelativeLayout(context).addViews(
                View(context),
                View(context)
            )
        ),
        View(context)
    )

    private fun createView3() = FrameLayout(context).addViews(
        LinearLayout(context).addViews(
            View(context),
            FrameLayout(context),
            RelativeLayout(context).addViews(
                TextView(context)
            )
        ),
        View(context)
    )

    private fun ViewGroup.addViews(vararg views: View): View {
        views.forEach { addView(it) }
        return this
    }
}
