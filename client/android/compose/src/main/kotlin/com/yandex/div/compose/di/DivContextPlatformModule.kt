package com.yandex.div.compose.di

import android.content.Context
import android.content.res.AssetManager
import com.yandex.div.compose.font.AndroidDivFontFamilyFactory
import com.yandex.div.compose.font.DivFontFamilyFactory
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import kotlin.math.max

@BindingContainer
internal interface DivContextPlatformModule {

    @Binds
    fun bindFontFamilyFactory(impl: AndroidDivFontFamilyFactory): DivFontFamilyFactory

    companion object {
        @DivContextScope
        @Provides
        fun provideAssetManager(context: Context): AssetManager = context.assets

        @DivContextScope
        @Provides
        @Named(Names.MAX_DISPLAY_SIZE)
        fun provideMaxDisplaySize(context: Context): Int {
            return context.resources.displayMetrics.let { max(it.widthPixels, it.heightPixels) }
        }
    }
}
