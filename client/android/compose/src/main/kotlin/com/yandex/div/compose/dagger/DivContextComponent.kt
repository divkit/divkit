package com.yandex.div.compose.dagger

import android.content.Context
import coil3.ImageLoader
import com.yandex.div.compose.DivComposeConfiguration
import com.yandex.div.compose.DivFontFamilyProvider
import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.context.DivViewContextStorage
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.images.ImageRequestListener
import com.yandex.div.compose.internal.DivDebugConfiguration
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
    val customViewFactories: Map<String, DivCustomViewFactory>
    val debugFeatures: DivDebugFeatures
    val extensionHandlers: Map<String, DivExtensionHandler>
    val fontFamilyProvider: DivFontFamilyProvider
    val imageLoader: ImageLoader
    val imageRequestListener: ImageRequestListener
    val viewContextStorage: DivViewContextStorage

    @get:Named(Names.HOST_VARIABLES)
    val variableController: DivVariableController

    fun viewComponent(): DivViewComponent.Builder

    @Component.Builder
    interface Builder {
        fun build(
            @BindsInstance baseContext: Context,
            @BindsInstance configuration: DivComposeConfiguration,
            @BindsInstance debugConfiguration: DivDebugConfiguration
        ): DivContextComponent
    }
}
