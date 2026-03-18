package com.yandex.div.core.preload

import android.net.Uri
import com.yandex.div.core.util.EnableAssertsRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for [PreloadingCompletionImpl].
 */
@RunWith(RobolectricTestRunner::class)
class PreloadingCompletionImplTest {

    @get:Rule
    val enableAssertsRule = EnableAssertsRule(true)

    private val preloadId = "test_preload_id"
    private var onCompleteInvocationCount = 0
    private val onComplete: () -> Unit = { onCompleteInvocationCount++ }

    private val underTest = PreloadingCompletionImpl(preloadId, onComplete)

    private val successResult = UriPreloadResult(Uri.parse("https://example.com/res"), null)
    private val errorResult = UriPreloadResult(
        Uri.parse("https://example.com/fail"),
        RuntimeException("load failed")
    )

    @Test
    fun `onCompleted sets isCompleted and result and invokes onComplete`() {
        underTest.onCompleted(successResult)

        Assert.assertTrue(underTest.isCompleted)
        Assert.assertSame(successResult, underTest.result)
        Assert.assertEquals(1, onCompleteInvocationCount)
    }

    @Test
    fun `isFailed is false when result has no errors`() {
        underTest.onCompleted(successResult)

        Assert.assertFalse(underTest.isFailed)
    }

    @Test
    fun `isFailed is false when result is null`() {
        Assert.assertFalse(underTest.isFailed)
    }

    @Test
    fun `isFailed is true when CompositeResult contains error`() {
        val compositeResult = CompositeResult(
            listOf(successResult, errorResult)
        )
        underTest.onCompleted(compositeResult)

        Assert.assertTrue(underTest.isFailed)
    }

    @Test
    fun `isFailed is false when CompositeResult has no errors`() {
        val compositeResult = CompositeResult(
            listOf(successResult, UriPreloadResult(Uri.parse("https://other.com"), null))
        )
        underTest.onCompleted(compositeResult)

        Assert.assertFalse(underTest.isFailed)
    }

    @Test(expected = AssertionError::class)
    fun `second onCompleted throws when assertions enabled`() {
        underTest.onCompleted(successResult)
        underTest.onCompleted(successResult)
    }
}
