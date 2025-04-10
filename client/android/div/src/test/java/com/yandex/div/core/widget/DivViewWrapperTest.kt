package com.yandex.div.core.widget

import android.app.Activity
import android.content.Context
import android.view.View
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivBorderDrawer
import com.yandex.div.core.view2.divs.widgets.DivBorderSupports
import com.yandex.div2.DivBorder
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivViewWrapperTest {
    private val context: Context = Robolectric.buildActivity(Activity::class.java).get()
    private val divView = mock<Div2View>()
    private val divBorder = DivBorder()
    private val viewWithBorder = object : DivBorderSupports, View(context) {

        override var needClipping: Boolean
            get() = true
            set(value) = Unit

        override fun getDivBorderDrawer(): DivBorderDrawer = divBorderDrawer

        override fun setBorder(bindingContext: BindingContext, border: DivBorder?, view: View) { }
    }
    private val divBorderDrawer: DivBorderDrawer = DivBorderDrawer(divView, viewWithBorder).apply {
        setBorder(divBorder, mock())
    }

    @Test
    fun `test DivViewWrapper has child's border`() {
        val divViewWrapper = DivViewWrapper(context)
        divViewWrapper.addView(viewWithBorder)
        assertSame(divBorder, divViewWrapper.getDivBorderDrawer()?.border)
        assertSame(divBorderDrawer, divViewWrapper.getDivBorderDrawer())
    }

    @Test
    fun `test DivViewWrapper has no border when no child`() {
        val divViewWrapper = DivViewWrapper(context)
        assertNull(divViewWrapper.getDivBorderDrawer()?.border)
        assertNull(divViewWrapper.getDivBorderDrawer())
    }
}
