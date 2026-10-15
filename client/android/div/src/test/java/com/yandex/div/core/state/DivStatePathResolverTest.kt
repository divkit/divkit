package com.yandex.div.core.state

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.dagger.Div2Component
import com.yandex.div.core.tooltip.DivTooltipController
import com.yandex.div.core.view2.Div2View
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@RunWith(AndroidJUnit4::class)
class DivStatePathResolverTest {

    private val tooltipController = mock<DivTooltipController>()
    private val div2Component = mock<Div2Component> {
        on { tooltipController } doReturn tooltipController
    }
    private val divView = mock<Div2View> {
        on { div2Component } doReturn div2Component
    }

    @Test
    fun `absolute path is parsed without tooltip lookup`() {
        val path = DivStatePathResolver.resolve(divView, "0/state/second")

        assertEquals(DivStatePath.parse("0/state/second"), path)
        verifyNoInteractions(tooltipController)
    }

    @Test
    fun `relative path is resolved by tooltip controller`() {
        val tooltipPath = DivStatePath.fromState(0).withNamespace("tooltip#1#t").append("state", "second", "second")
        whenever(tooltipController.resolveStatePath(divView, "state/second")).doReturn(tooltipPath)

        assertEquals(tooltipPath, DivStatePathResolver.resolve(divView, "state/second"))
    }

    @Test
    fun `relative path with numeric first segment is resolved by tooltip controller`() {
        val tooltipPath = DivStatePath.fromState(0).withNamespace("tooltip#1#t").append("1", "second", "second")
        whenever(tooltipController.resolveStatePath(divView, "1/second")).doReturn(tooltipPath)

        assertEquals(tooltipPath, DivStatePathResolver.resolve(divView, "1/second"))
    }

    @Test
    fun `relative path for non Div2View facade throws`() {
        assertFailsWith<PathFormatException> {
            DivStatePathResolver.resolve(mock<DivViewFacade>(), "state/second")
        }
    }
}
