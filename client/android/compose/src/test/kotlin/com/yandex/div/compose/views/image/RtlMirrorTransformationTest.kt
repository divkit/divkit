package com.yandex.div.compose.views.image

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.size.Size
import coil3.transform.Transformation
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.test.data.rtlMirrorFilter
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class RtlMirrorTransformationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun `mirrors bitmap horizontally`() = runTest {
        val input = Bitmap.createBitmap(2, 1, Bitmap.Config.ARGB_8888).apply {
            setPixel(0, 0, Color.RED)
            setPixel(1, 0, Color.BLUE)
        }

        val output = RtlMirrorTransformation.transform(input, Size.ORIGINAL)

        val outputPixels = IntArray(2)
        output.getPixels(outputPixels, 0, output.width, 0, 0, output.width, output.height)
        assertContentEquals(intArrayOf(Color.BLUE, Color.RED), outputPixels)
        assertEquals(input.width, output.width)
        assertEquals(input.height, output.height)
    }

    @Test
    fun `updates transformations when layout direction changes`() {
        var layoutDirection by mutableStateOf(LayoutDirection.Ltr)
        var transformations = emptyList<Transformation>()
        val filters = listOf(rtlMirrorFilter())

        composeRule.setContentWithDivContext(DivConfiguration()) {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                transformations = filters.observedTransformations()
            }
        }

        composeRule.runOnIdle {
            assertEquals(emptyList(), transformations)
            layoutDirection = LayoutDirection.Rtl
        }
        composeRule.runOnIdle {
            assertEquals(listOf(RtlMirrorTransformation), transformations)
            layoutDirection = LayoutDirection.Ltr
        }
        composeRule.runOnIdle {
            assertEquals(emptyList(), transformations)
        }
    }
}
