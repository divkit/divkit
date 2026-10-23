package com.yandex.div.core.view2

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.util.binding.BindingDispatcher
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class WorkerVisibilityTrackingBatchTest {
    private val card = data(text(text = "Current"))
    private val replacementCard = data(text(text = "Replacement"))
    private val view = mock<Div2View> {
        on { divData } doReturn card
        on { dataTag } doReturn DivDataTag("current")
    }
    private val dispatcher = mock<BindingDispatcher> {
        on { currentGeneration } doReturn 5
    }
    private val scans = mutableListOf<String>()
    private val batch = WorkerVisibilityTrackingBatch(view, dispatcher) { scans += "current document" }

    @Test
    fun `a scan is not deferred outside collection`() {
        assertFalse(batch.tryDeferScan())
    }

    @Test
    fun `one scan runs when the document is installed before its first request`() {
        doReturn(null).whenever(view).divData

        batch.collect {
            doReturn(card).whenever(view).divData
            batch.tryDeferScan()
            batch.tryDeferScan()
        }

        assertEquals(listOf("current document"), scans)
    }

    @Test
    fun `a replacement request supersedes the previous document pending scan`() {
        batch.collect {
            batch.tryDeferScan()
            doReturn(replacementCard).whenever(view).divData
            batch.tryDeferScan()
        }

        assertEquals(listOf("current document"), scans)
    }

    @Test
    fun `a scan is not deferred when generation changes before the first request`() {
        val deferred = batch.collect {
            doReturn(6).whenever(dispatcher).currentGeneration
            batch.tryDeferScan()
        }

        assertFalse(deferred)
    }

    @Test
    fun `a nested replacement request waits until the outer collection ends`() {
        batch.collect {
            doReturn(replacementCard).whenever(view).divData
            batch.collect { batch.tryDeferScan() }
            scans += "outer collection continues"
        }

        assertEquals(listOf("outer collection continues", "current document"), scans)
    }

    @Test
    fun `one visibility scan runs when requests repeat in one collection`() {
        batch.collect {
            batch.tryDeferScan()
            batch.tryDeferScan()
        }

        assertEquals(listOf("current document"), scans)
    }

    @Test
    fun `one visibility scan runs when nested collections request it`() {
        batch.collect {
            batch.tryDeferScan()
            batch.collect { batch.tryDeferScan() }
        }

        assertEquals(listOf("current document"), scans)
    }

    @Test
    fun `pending visibility scan runs before a callback following flush`() {
        batch.collect {
            batch.tryDeferScan()
            batch.flush()
            scans += "callback"
        }

        assertEquals(listOf("current document", "callback"), scans)
    }

    @Test
    fun `pending visibility scan is discarded when collection is cancelled`() {
        batch.collect {
            batch.tryDeferScan()
            batch.discard()
        }

        assertEquals(emptyList(), scans)
    }

    @Test
    fun `pending visibility scan is discarded when collection fails`() {
        runCatching {
            batch.collect {
                batch.tryDeferScan()
                error("Binding failed")
            }
        }

        assertEquals(emptyList(), scans)
    }

    @Test
    fun `visibility scan runs when the next collection succeeds after failure`() {
        runCatching {
            batch.collect {
                batch.tryDeferScan()
                error("Binding failed")
            }
        }

        batch.collect { batch.tryDeferScan() }

        assertEquals(listOf("current document"), scans)
    }

    @Test
    fun `a new visibility scan runs when requested after flush`() {
        batch.collect {
            batch.tryDeferScan()
            batch.flush()
            batch.tryDeferScan()
        }

        assertEquals(listOf("current document", "current document"), scans)
    }

    @Test
    fun `pending visibility scan is discarded when its tag changes`() {
        batch.collect {
            batch.tryDeferScan()
            whenever(view.dataTag).thenReturn(DivDataTag("replacement"))
        }

        assertEquals(emptyList(), scans)
    }

    @Test
    fun `pending visibility scan is discarded when its generation changes`() {
        batch.collect {
            batch.tryDeferScan()
            whenever(dispatcher.currentGeneration).thenReturn(6)
        }

        assertEquals(emptyList(), scans)
    }

    @Test
    fun `pending visibility scan is discarded when its document changes`() {
        batch.collect {
            batch.tryDeferScan()
            whenever(view.divData).thenReturn(data(text(text = "Replacement")))
        }

        assertEquals(emptyList(), scans)
    }
}
