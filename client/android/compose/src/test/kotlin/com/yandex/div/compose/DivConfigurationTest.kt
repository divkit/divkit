package com.yandex.div.compose

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.patch.DivPatchDownloader
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class DivConfigurationTest {

    @Test
    fun `builder uses custom patchDownloader when set before networkClient`() {
        val customPatchDownloader = mock<DivPatchDownloader>()

        val configuration = divConfiguration {
            patchDownloader = customPatchDownloader
            networkClient = mock()
        }

        assertSame(customPatchDownloader, configuration.patchDownloader)
    }
}
