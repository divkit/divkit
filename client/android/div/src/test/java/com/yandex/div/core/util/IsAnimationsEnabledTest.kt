package com.yandex.div.core.util

import android.app.Activity
import android.os.Build
import android.provider.Settings
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.view2.Div2View
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class IsAnimationsEnabledTest {

    private val imageLoader = mock<DivImageLoader>()
    private val activity = Robolectric.buildActivity(Activity::class.java).get()

    private fun divViewWith(enabled: Boolean): Div2View {
        val provider = object : DivAnimationsEnabledProvider {
            override val animationsEnabled = MutableStateFlow(enabled)
        }
        val configuration = DivConfiguration.Builder(imageLoader)
            .animationsEnabledProvider(provider)
            .build()
        return Div2View(Div2Context(baseContext = activity, configuration = configuration))
    }

    @Test
    fun `enabled by default`() {
        val configuration = DivConfiguration.Builder(imageLoader).build()
        val div2View = Div2View(Div2Context(baseContext = activity, configuration = configuration))

        assertTrue(div2View.div2Component.animationsEnabledController.isEnabled())
    }

    @Test
    fun `disabled when provider returns false`() {
        assertFalse(divViewWith(false).div2Component.animationsEnabledController.isEnabled())
    }

    @Test
    fun `enabled when provider returns true`() {
        assertTrue(divViewWith(true).div2Component.animationsEnabledController.isEnabled())
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.S])
    fun `disabled when system animations are off even if provider returns true`() {
        setSystemAnimatorScale(0f)

        assertFalse(divViewWith(true).div2Component.animationsEnabledController.isEnabled())
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.S])
    fun `enabled when system animations are on and provider returns true`() {
        setSystemAnimatorScale(1f)

        assertTrue(divViewWith(true).div2Component.animationsEnabledController.isEnabled())
    }

    private fun setSystemAnimatorScale(scale: Float) {
        Settings.Global.putFloat(
            activity.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            scale,
        )
    }
}
