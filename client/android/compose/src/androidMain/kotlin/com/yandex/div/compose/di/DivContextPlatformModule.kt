package com.yandex.div.compose.di

import android.content.Context
import android.content.res.AssetManager
import com.yandex.div.compose.animation.AnimationConfiguration
import com.yandex.div.compose.font.AndroidFontFamilyFactory
import com.yandex.div.compose.font.AndroidLineMetricsMeasurer
import com.yandex.div.compose.font.FontFamilyFactory
import com.yandex.div.compose.font.LineMetricsMeasurer
import com.yandex.div.compose.images.AndroidImageLoaderConfiguration
import com.yandex.div.compose.images.ImageHttpClientFactory
import com.yandex.div.compose.images.PlatformImageLoaderConfiguration
import com.yandex.div.compose.tooltips.AndroidTooltipDrawObserverFactory
import com.yandex.div.compose.tooltips.TooltipDrawObserverFactory
import com.yandex.div.compose.views.slider.AndroidSliderTextPainterFactory
import com.yandex.div.compose.views.slider.SliderTextPainterFactory
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div.render.common.animation.SystemAnimationConfiguration
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import io.ktor.client.HttpClient
import kotlin.math.max

@BindingContainer
internal interface DivContextPlatformModule {

    @Binds
    fun bindFontFamilyFactory(impl: AndroidFontFamilyFactory): FontFamilyFactory

    @Binds
    fun bindLineMetricsMeasurer(impl: AndroidLineMetricsMeasurer): LineMetricsMeasurer

    @Binds
    fun bindSliderTextPainterFactory(
        impl: AndroidSliderTextPainterFactory
    ): SliderTextPainterFactory

    @Binds
    fun bindImageLoaderConfiguration(
        impl: AndroidImageLoaderConfiguration
    ): PlatformImageLoaderConfiguration

    @Binds
    fun bindTooltipDrawObserverFactory(
        impl: AndroidTooltipDrawObserverFactory
    ): TooltipDrawObserverFactory

    companion object {
        @DivContextScope
        @Provides
        fun provideAssetManager(context: Context): AssetManager = context.assets

        @DivContextScope
        @Provides
        fun provideImageHttpClient(): HttpClient = ImageHttpClientFactory.create()

        @DivContextScope
        @Provides
        @Named(Names.MAX_DISPLAY_SIZE)
        fun provideMaxDisplaySize(context: Context): Int {
            return context.resources.displayMetrics.let { max(it.widthPixels, it.heightPixels) }
        }

        @DivContextScope
        @Provides
        fun provideAnimationConfiguration(
            context: Context,
            animationsEnabledProvider: DivAnimationsEnabledProvider,
        ): AnimationConfiguration = AnimationConfiguration(
            systemAnimationsEnabled = SystemAnimationConfiguration(context)::isEnabled,
            provider = object : AnimationConfiguration.Provider {
                override val animationsEnabled get() = animationsEnabledProvider.animationsEnabled

                override fun isAnimationsEnabled(): Boolean = animationsEnabledProvider.isAnimationsEnabled()
            },
        )
    }
}
