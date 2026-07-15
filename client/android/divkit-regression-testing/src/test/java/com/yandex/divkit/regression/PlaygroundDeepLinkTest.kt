package com.yandex.divkit.regression

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlaygroundDeepLinkTest {

    @Test
    fun `parseTest by valid id returns ByCaseId`() {
        val query = PlaygroundDeepLink.parseTest(Uri.parse("playground://test?id=42"))
        assertTrue(query is PlaygroundDeepLink.TestQuery.ByCaseId)
        assertEquals(42, (query as PlaygroundDeepLink.TestQuery.ByCaseId).caseId)
    }

    @Test
    fun `parseTest by invalid id returns null`() {
        assertNull(PlaygroundDeepLink.parseTest(Uri.parse("playground://test?id=abc")))
    }

    @Test
    fun `parseTest by title returns ByTitle`() {
        val query = PlaygroundDeepLink.parseTest(Uri.parse("playground://test?title=Variable%20test"))
        assertTrue(query is PlaygroundDeepLink.TestQuery.ByTitle)
        assertEquals("Variable test", (query as PlaygroundDeepLink.TestQuery.ByTitle).title)
    }

    @Test
    fun `parseTest without params returns null`() {
        assertNull(PlaygroundDeepLink.parseTest(Uri.parse("playground://test")))
    }

    @Test
    fun `parseTest wrong scheme returns null`() {
        assertNull(PlaygroundDeepLink.parseTest(Uri.parse("other://test?id=42")))
    }

    @Test
    fun `parseTest unknown host returns null`() {
        assertNull(PlaygroundDeepLink.parseTest(Uri.parse("playground://unknown?id=42")))
    }
}
