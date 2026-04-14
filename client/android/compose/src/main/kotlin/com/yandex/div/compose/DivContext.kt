package com.yandex.div.compose

import android.content.Context
import android.content.ContextWrapper
import androidx.annotation.VisibleForTesting
import com.yandex.div.compose.context.DivViewContext
import com.yandex.div.compose.dagger.DivContextComponent
import com.yandex.div.compose.dagger.`Yatagan$DivContextComponent`
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.compose.internal.DivDebugFeatures
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div2.DivData

/**
 * An implementation of [android.content.Context] that must be used for composing [DivView]s.
 *
 * Example usage:
 *
 *    val configuration = DivComposeConfiguration()
 *    val divContext = DivContext(activity, configuration)
 */
@ExperimentalApi
class DivContext private constructor(
    internal val component: DivContextComponent
) : ContextWrapper(component.baseContext) {

    /**
     * Creates a [DivContext] with provided [DivComposeConfiguration].
     */
    constructor(
        baseContext: Context,
        configuration: DivComposeConfiguration
    ) : this(createComponent(baseContext, configuration, DivDebugConfiguration()))

    /**
     * Creates a [DivContext] with provided [DivComposeConfiguration] and [DivDebugConfiguration].
     *
     * Do not use this constructor in production environment.
     */
    @InternalApi
    constructor(
        baseContext: Context,
        configuration: DivComposeConfiguration,
        debugConfiguration: DivDebugConfiguration
    ) : this(createComponent(baseContext, configuration, debugConfiguration))

    @InternalApi
    @VisibleForTesting
    val debugFeatures: DivDebugFeatures
        get() = component.debugFeatures

    /**
     * Removes [DivView] context associated with the given [com.yandex.div2.DivData].
     */
    fun clearViewContext(data: DivData) {
        component.viewContextStorage.remove(data)
    }

    internal fun getViewContext(data: DivData): DivViewContext {
        component.viewContextStorage.get(data)?.let {
            return it
        }

        return DivViewContext(
            data = data,
            component = component.viewComponent().build()
        ).also {
            component.viewContextStorage.put(data, it)
        }
    }
}

private fun createComponent(
    baseContext: Context,
    configuration: DivComposeConfiguration,
    debugConfiguration: DivDebugConfiguration
): DivContextComponent {
    return `Yatagan$DivContextComponent`.builder().build(
        baseContext = baseContext,
        configuration = configuration,
        debugConfiguration = debugConfiguration
    )
}
