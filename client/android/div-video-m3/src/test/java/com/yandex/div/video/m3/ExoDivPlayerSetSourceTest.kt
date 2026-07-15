package com.yandex.div.video.m3

import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerPlaybackConfig
import com.yandex.div.core.player.DivVideoSource
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExoDivPlayerSetSourceTest {

    private val context = ApplicationProvider.getApplicationContext<android.app.Application>()

    private val defaultConfig = DivPlayerPlaybackConfig()

    private val validSource = DivVideoSource(
        url = Uri.parse("https://example.com/video.mp4"),
        mimeType = "video/mp4"
    )

    private lateinit var player: ExoDivPlayer
    private var fatalCalled = false
    private lateinit var observer: DivPlayer.Observer

    @Before
    fun setUp() {
        fatalCalled = false
        observer = object : DivPlayer.Observer {
            override fun onFatal(error: Throwable) {
                fatalCalled = true
            }
        }
        player = ExoDivPlayer(
            context = context,
            src = listOf(validSource),
            config = defaultConfig,
            cacheDataSourceFactory = null
        )
        player.addObserver(observer)
    }

    @After
    fun tearDown() {
        player.release()
    }

    @Test
    fun `setSource with non-empty list does not call onFatal on registered observer`() {
        player.setSource(listOf(validSource), defaultConfig)

        assertFalse(
            "onFatal must NOT be called when setSource is given a non-empty source list",
            fatalCalled
        )
    }

    @Test
    fun `setSource with empty list calls onFatal on registered observer`() {
        player.setSource(emptyList(), defaultConfig)

        assertTrue(
            "onFatal MUST be called when setSource is given an empty source list",
            fatalCalled
        )
    }
}
