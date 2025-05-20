package com.yandex.div.core

import android.graphics.Typeface
import com.yandex.div.core.font.DivTypefaceProvider
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.getTypefaceValue
import com.yandex.div2.DivFontWeight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivTypefaceProviderTest {
    private val lightStub = buildStubTypeface(300)
    private val regularStub = buildStubTypeface(400)
    private val mediumStub = buildStubTypeface(500)
    private val boldStub = buildStubTypeface(700)
    private val heavyStub = buildStubTypeface(900)

    private val testTypefaceProvider = object : DivTypefaceProvider {
        override fun getLight(): Typeface = lightStub
        override fun getRegular(): Typeface = regularStub
        override fun getMedium(): Typeface = mediumStub
        override fun getBold(): Typeface = boldStub
        override fun getTypefaceFor(weight: Int): Typeface? {
            return when (weight) {
                900 -> heavyStub
                else -> super.getTypefaceFor(weight)
            }
        }
    }

    @Test
    fun `typeface stubs not equals`() {
        listOf(lightStub, regularStub, mediumStub, boldStub)
            .zipWithNext { a, b ->
                assertNotEquals(a, b)
            }
    }

    @Test
    fun `check light value`() {
        assertEquals(lightStub, getTypefaceFrom(weight = DivFontWeight.LIGHT))
    }

    @Test
    fun `check regular value`() {
        assertEquals(regularStub, getTypefaceFrom(weight = DivFontWeight.REGULAR))
    }

    @Test
    fun `check medium value`() {
        assertEquals(mediumStub, getTypefaceFrom(weight = DivFontWeight.MEDIUM))
    }

    @Test
    fun `check bold value`() {
        assertEquals(boldStub, getTypefaceFrom(weight = DivFontWeight.BOLD))
    }

    @Test
    fun `check 400 value`() {
        assertEquals(regularStub, getTypefaceFrom(value = 400))
    }

    @Test
    fun `check 700 value`() {
        assertEquals(boldStub, getTypefaceFrom(value = 700))
    }

    @Test
    fun `check 900 value`() {
        assertEquals(heavyStub, getTypefaceFrom(value = 900))
    }

    @Test
    fun `unhandled value coerced to nearest`() {
        assertEquals(boldStub, getTypefaceFrom(value = 600))
    }

    @Test
    fun `default is 400`() {
        assertEquals(regularStub, getTypefaceFrom(weight = null, value = null))
    }

    private fun buildStubTypeface(weight: Int) = Typeface.create(Typeface.DEFAULT, weight, false)

    private fun getTypefaceFrom(weight: DivFontWeight? = null, value: Long? = null): Typeface {
        val typefaceValue = getTypefaceValue(weight, value?.toIntSafely())
        return testTypefaceProvider.getTypefaceFor(typefaceValue) ?: Typeface.DEFAULT
    }
}
