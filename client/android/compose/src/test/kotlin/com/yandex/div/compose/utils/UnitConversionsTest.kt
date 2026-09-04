package com.yandex.div.compose.utils

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div2.DivSizeUnit
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class UnitConversionsTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `text size units preserve physical dimensions with font scaling`() {
        val density = Density(density = 2f, fontScale = 2f)
        var dpTextUnit = TextUnit.Unspecified
        var spTextUnit = TextUnit.Unspecified
        var pxTextUnit = TextUnit.Unspecified

        rule.setContent {
            CompositionLocalProvider(LocalDensity provides density) {
                dpTextUnit = 10.toTextUnit(DivSizeUnit.DP)
                spTextUnit = 10.toTextUnit(DivSizeUnit.SP)
                pxTextUnit = 10.toTextUnit(DivSizeUnit.PX)
            }
        }

        assertEquals(5.sp, dpTextUnit)
        assertEquals(10.sp, spTextUnit)
        assertEquals(2.5.sp, pxTextUnit)
    }

    @Test
    fun `pixel conversion applies font scaling only to sp`() {
        rule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(density = 2f, fontScale = 2f)) {
                assertEquals(20f, 10f.toPx(DivSizeUnit.DP))
                assertEquals(40f, 10f.toPx(DivSizeUnit.SP))
                assertEquals(10f, 10f.toPx(DivSizeUnit.PX))
            }
        }
    }
}
