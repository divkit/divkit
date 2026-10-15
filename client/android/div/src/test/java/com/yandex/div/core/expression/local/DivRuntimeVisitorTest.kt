package com.yandex.div.core.expression.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.state.DivStateManager
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.test.data.container
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivState
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivRuntimeVisitorTest {

    private val stateManager = mock<DivStateManager> {
        on { getState(any<DivState>(), any(), any()) } doReturn "s1"
    }
    private val runtimeStore = mock<RuntimeStore> {
        on { rootRuntime } doReturn ExpressionsRuntime(mock())
        on { getOrCreateRuntime(any(), any(), any()) } doReturn ExpressionsRuntime(mock())
    }
    private val divView = mock<Div2View> {
        on { runtimeStore } doReturn runtimeStore
    }
    private val inner = Div.State(
        state(id = "inner", states = listOf(DivState.State(stateId = "x", div = text(text = "x"))))
    )
    private val outer = Div.State(
        state(id = "outer", states = listOf(DivState.State(stateId = "s1", div = container(items = listOf(inner)))))
    )

    @Test
    fun `nested state inside root state is looked up by state id`() {
        val rootPath = DivStatePath.fromState(0).withNamespace("tooltip#1#t").appendDiv("tooltip#t#outer")

        DivRuntimeVisitor(stateManager, mock()).createAndAttachRuntimes(outer, rootPath, divView)

        val keys = argumentCaptor<String>()
        verify(stateManager, times(2)).getState(any<DivState>(), any(), keys.capture())
        assertEquals(listOf("tooltip#1#t/0/outer", "tooltip#1#t/0/outer/s1/inner"), keys.allValues)
    }
}
