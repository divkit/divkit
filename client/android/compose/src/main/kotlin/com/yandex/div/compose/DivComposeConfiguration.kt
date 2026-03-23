package com.yandex.div.compose

import android.content.Context
import coil3.ImageLoader
import coil3.request.allowHardware
import com.yandex.div.compose.actions.DivActionData
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.actions.DivCustomActionHandler
import com.yandex.div.compose.dagger.Names
import com.yandex.div.compose.dagger.`Yatagan$DivContextComponent`
import com.yandex.div.compose.internal.ImageLoaderProvider
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides
import javax.inject.Named

@Module
@PublicApi
class DivComposeConfiguration(
    @get:Provides
    val customActionHandler: DivCustomActionHandler = defaultCustomActionHandler,

    @get:Provides
    val reporter: DivReporter = DivReporter(),

    @get:Provides
    @get:Named(Names.HOST_VARIABLES)
    val variableController: DivVariableController = DivVariableController(),

    @get:Provides
    @property:InternalApi
    val imageLoaderProvider: ImageLoaderProvider = defaultImageLoaderProvider,
)

fun DivComposeConfiguration.createContext(baseContext: Context): DivContext {
    val contextComponent = `Yatagan$DivContextComponent`.builder()
        .baseContext(baseContext)
        .configuration(this)
        .build()
    return DivContext(contextComponent)
}

private val defaultImageLoaderProvider = ImageLoaderProvider { context ->
    ImageLoader.Builder(context = context)
        .allowHardware(false)
        .build()
}

private val defaultCustomActionHandler = object : DivCustomActionHandler {
    override fun handle(
        context: DivActionHandlingContext,
        action: DivActionData
    ) = Unit
}
