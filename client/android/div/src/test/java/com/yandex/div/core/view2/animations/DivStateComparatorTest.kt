package com.yandex.div.core.view2.animations

import com.yandex.div.core.view2.divs.UnitTestData
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

const val DIV_STATE_DIR = "div-state"

@RunWith(RobolectricTestRunner::class)
class DivStateComparatorTest {

    @Test
    fun `test replaceable with itself`() {
        val data1 = readDiv("text_state.json")
        val data2 = readDiv("text_state.json")

        Assert.assertTrue(DivComparator.isDivDataReplaceable(
            data1,
            data2,
            0,
            ExpressionResolver.EMPTY,
            ExpressionResolver.EMPTY
        ))
    }

    @Test
    fun `test not replaceable missing state`() {
        val data1 = readDiv("text_state.json")
        val data2 = readDiv("text_state.json", stateId = 1)

        Assert.assertFalse(DivComparator.isDivDataReplaceable(
            data1,
            data2,
            1,
            ExpressionResolver.EMPTY,
            ExpressionResolver.EMPTY
        ))
    }

    @Test
    fun `test not replaceable structure difference`() {
        val data1 = readDiv("text_state.json")
        val data2 = readDiv("gallery_text_container.json")

        Assert.assertFalse(DivComparator.isDivDataReplaceable(
            data1,
            data2,
            0,
            ExpressionResolver.EMPTY,
            ExpressionResolver.EMPTY
        ))
    }

    private fun readDiv(resource: String, stateId: Long = 0) : DivData {
        val state = DivData.State(UnitTestData(DIV_STATE_DIR, resource).div, stateId)
        return DivData(logId = "log_id", states = listOf(state))
    }
}
