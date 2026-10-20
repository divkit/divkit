package com.yandex.div.compose.text

import android.graphics.Typeface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.font.FontWeight
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.font.DivFontSource
import com.yandex.div.compose.font.DivFontSourceProvider
import com.yandex.div.compose.setContentWithDivContext
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertNotSame

@RunWith(AndroidJUnit4::class)
class FontFamilyTest {
    @get:Rule
    val rule = createComposeRule()

    private val configuration = DivConfiguration(
        fontSourceProvider = FamilyFontSourceProvider(),
        reporter = TestReporter(),
    )

    @Test
    fun `remembered typeface follows a font family change`() {
        val fontFamily = mutableStateOf("serif")
        var typeface: Typeface? = null
        rule.setContentWithDivContext(configuration) {
            typeface = rememberTypeface(fontFamily = fontFamily.value, fontWeight = null, fontWeightValue = null)
        }
        val serifTypeface = typeface

        fontFamily.value = "monospace"
        rule.waitForIdle()

        assertNotSame(serifTypeface, typeface)
    }

    private class FamilyFontSourceProvider : DivFontSourceProvider {
        override fun getFontSource(fontFamilyName: String?, weight: FontWeight): DivFontSource {
            return DivFontSource.Typeface(if (fontFamilyName == "monospace") Typeface.MONOSPACE else Typeface.SERIF)
        }
    }
}
