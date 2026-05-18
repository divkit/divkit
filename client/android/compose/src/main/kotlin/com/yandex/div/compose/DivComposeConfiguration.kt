package com.yandex.div.compose

import android.graphics.Typeface
import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.compose.actions.DivExternalActionHandler
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.dagger.Names
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.font.DivFontSource
import com.yandex.div.compose.font.DivFontSourceProvider
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides
import javax.inject.Named

/**
 * Entry point of DivKit `compose` library. Provides configuration for composing [DivView]s.
 *
 * Example usage:
 *
 *    val configuration = DivComposeConfiguration(
 *        reporter = MyReporter()
 *    )
 *    val divContext = DivContext(baseContext = activity, configuration = configuration)
 *    ComposeView(divContext).setContent {
 *        DivView(data = data)
 *    }
 */
@Module
@ExperimentalApi
class DivComposeConfiguration(
    @get:Provides
    val actionHandler: DivExternalActionHandler = defaultActionHandler,

    @get:Provides
    val customViewFactories: Map<String, DivCustomViewFactory> = emptyMap(),

    @get:Provides
    val extensionHandlers: Map<String, DivExtensionHandler> = emptyMap(),

    @get:Provides
    val fontSourceProvider: DivFontSourceProvider = defaultFontSourceProvider,

    @get:Provides
    val reporter: DivReporter = DivReporter(),

    @get:Provides
    @get:Named(Names.HOST_VARIABLES)
    val variableController: DivVariableController = DivVariableController(),
)

private val defaultActionHandler = object : DivExternalActionHandler {}

private val defaultFontSourceProvider = object : DivFontSourceProvider {
    override fun getFontSource(fontFamilyName: String?, weight: FontWeight): DivFontSource {
        return DivFontSource.Typeface(Typeface.DEFAULT)
    }
}
