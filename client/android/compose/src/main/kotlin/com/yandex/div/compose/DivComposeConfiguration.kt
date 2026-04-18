package com.yandex.div.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.compose.actions.DivExternalActionHandler
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.dagger.Names
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
    val customViewFactory: DivCustomViewFactory = defaultCustomViewFactory,

    @get:Provides
    val fontFamilyProvider: DivFontFamilyProvider = defaultFontFamilyProvider,

    @get:Provides
    val reporter: DivReporter = DivReporter(),

    @get:Provides
    @get:Named(Names.HOST_VARIABLES)
    val variableController: DivVariableController = DivVariableController(),
)

private val defaultActionHandler = object : DivExternalActionHandler {}

private val defaultCustomViewFactory = object : DivCustomViewFactory {
    @Composable
    override fun Content(
        environment: DivCustomEnvironment,
        modifier: Modifier,
    ) = Unit
}

private val defaultFontFamilyProvider = object : DivFontFamilyProvider {
    override fun getFontFamily(fontFamilyName: String?, weight: FontWeight): FontFamily {
        return FontFamily.Default
    }
}
