package com.yandex.div.compose.dagger

import android.content.Context
import coil3.ImageLoader
import com.yandex.div.compose.DivComposeConfiguration
import com.yandex.div.compose.DivFontFamilyProvider
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.actions.VisibilityActionTracker
import com.yandex.div.compose.context.DivViewContextStorage
import com.yandex.div.compose.internal.DivDebugFeatures
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.yatagan.BindsInstance
import com.yandex.yatagan.Component
import javax.inject.Named

@DivContextScope
@Component(
    modules = [
        DivComposeConfiguration::class,
        DivContextModule::class
    ]
)
internal interface DivContextComponent {

    val actionHandler: DivActionHandler
    val baseContext: Context
    val debugFeatures: DivDebugFeatures
    val fontFamilyProvider: DivFontFamilyProvider
    val imageLoader: ImageLoader
    val reporter: DivReporter
    val viewContextStorage: DivViewContextStorage
    val visibilityActionTracker: VisibilityActionTracker

    @get:Named(Names.HOST_VARIABLES)
    val variableController: DivVariableController

    fun localComponent(): DivLocalComponent.Builder

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun baseContext(baseContext: Context): Builder

        @BindsInstance
        fun configuration(configuration: DivComposeConfiguration): Builder

        fun build(): DivContextComponent
    }
}
