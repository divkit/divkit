package com.yandex.div.compose.dagger

import android.content.Context
import com.yandex.div.compose.DivComposeConfiguration
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.yatagan.BindsInstance
import com.yandex.yatagan.Component

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
    val reporter: DivReporter

    fun viewComponent(): DivViewComponent.Builder

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun baseContext(baseContext: Context): Builder

        @BindsInstance
        fun configuration(configuration: DivComposeConfiguration): Builder

        fun build(): DivContextComponent
    }
}
