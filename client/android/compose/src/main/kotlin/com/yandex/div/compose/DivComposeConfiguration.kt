package com.yandex.div.compose

import android.content.Context
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.compose.actions.DivExternalActionHandler
import com.yandex.div.compose.dagger.Names
import com.yandex.div.compose.dagger.`Yatagan$DivContextComponent`
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.core.annotations.InternalApi
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
 *    val divContext = configuration.createContext(baseContext = activity)
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
    @property:InternalApi
    val debugConfiguration: DivDebugConfiguration = DivDebugConfiguration(),

    @get:Provides
    val fontFamilyProvider: DivFontFamilyProvider = defaultFontFamilyProvider,

    @get:Provides
    val reporter: DivReporter = DivReporter(),

    @get:Provides
    @get:Named(Names.HOST_VARIABLES)
    val variableController: DivVariableController = DivVariableController(),
)

fun DivComposeConfiguration.createContext(baseContext: Context): DivContext {
    val contextComponent = `Yatagan$DivContextComponent`.builder()
        .baseContext(baseContext)
        .configuration(this)
        .build()
    return DivContext(contextComponent)
}

private val defaultActionHandler = object : DivExternalActionHandler {}

private val defaultFontFamilyProvider = object : DivFontFamilyProvider {
    override fun getFontFamily(fontFamilyName: String?, weight: FontWeight): FontFamily {
        return FontFamily.Default
    }
}
