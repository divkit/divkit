package com.yandex.div.core.network

import android.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivKit
import com.yandex.div.core.DivKitConfiguration
import com.yandex.div.core.downloader.DivDownloader
import com.yandex.div.core.images.DivImageLoader
import org.junit.After
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivNetworkDependencyInjectionTest {

    @After
    fun resetSingleton() {
        DivKit.resetSingletonForTesting()
    }

    @Test
    fun `global network client provides default div downloader`() {
        DivKit.configure(
            DivKitConfiguration.Builder()
                .networkClient(mock<DivNetworkClient>())
                .build()
        )
        val context = Div2Context(
            ContextThemeWrapper(ApplicationProvider.getApplicationContext(), 0),
            DivConfiguration.Builder(mock<DivImageLoader>()).build(),
        )

        assertTrue(context.div2Component.divDownloader is DivNetworkDivDownloader)
    }

    @Test
    fun `explicit div downloader takes precedence over global network client`() {
        DivKit.configure(
            DivKitConfiguration.Builder()
                .networkClient(mock<DivNetworkClient>())
                .build()
        )
        val configuration = DivConfiguration.Builder(mock<DivImageLoader>())
            .divDownloader(DivDownloader.STUB)
            .build()
        val context = Div2Context(
            ContextThemeWrapper(ApplicationProvider.getApplicationContext(), 0),
            configuration,
        )

        assertSame(DivDownloader.STUB, context.div2Component.divDownloader)
    }
}
