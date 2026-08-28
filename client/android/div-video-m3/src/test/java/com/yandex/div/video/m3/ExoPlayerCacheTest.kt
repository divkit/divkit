package com.yandex.div.video.m3

import android.net.Uri
import androidx.media3.datasource.DataSpec
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.DivNetworkResponse
import com.yandex.div.core.network.DivNetworkResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExoPlayerCacheTest {

    @Test
    fun `video cache uses configured network client`() {
        val networkClient = mock<DivNetworkClient>()

        val cache = ExoPlayerCache(ApplicationProvider.getApplicationContext(), networkClient)

        assertSame(networkClient, cache.networkClient)
    }

    @Test
    fun `video request goes through configured network client`() {
        val response = FakeResponse("video".toByteArray())
        lateinit var request: DivNetworkRequest
        val networkClient = DivNetworkClient { networkRequest ->
            request = networkRequest
            response
        }
        val cache = ExoPlayerCache(ApplicationProvider.getApplicationContext(), networkClient)
        val dataSource = cache.cacheDataSourceFactory.createDataSource()

        dataSource.open(DataSpec(Uri.parse("https://example.com/video-${System.nanoTime()}")))
        val buffer = ByteArray(5)

        assertEquals(5, dataSource.read(buffer, 0, buffer.size))
        assertEquals("GET", request.method)
        dataSource.close()
    }

    private class FakeResponse(body: ByteArray) : DivNetworkResponse {
        override val url = "https://example.com/video"
        override val code = 200
        override val contentType: String? = null
        override val body: DivNetworkResponseBody = DivNetworkResponseBody.fromBytes(body)
        override fun headers(name: String): List<String> = emptyList()
        override fun close() = Unit
    }
}
