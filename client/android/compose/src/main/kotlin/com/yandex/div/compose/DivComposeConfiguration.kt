package com.yandex.div.compose

import android.content.Context
import com.yandex.div.compose.dagger.`Yatagan$DivContextComponent`
import com.yandex.div.core.annotations.PublicApi
import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides

@Module
@PublicApi
class DivComposeConfiguration(
    @get:Provides val reporter: DivReporter = DivReporter()
)

fun DivComposeConfiguration.createContext(baseContext: Context): DivContext {
    return `Yatagan$DivContextComponent`.builder()
        .baseContext(baseContext)
        .configuration(this)
        .build()
        .context
}
