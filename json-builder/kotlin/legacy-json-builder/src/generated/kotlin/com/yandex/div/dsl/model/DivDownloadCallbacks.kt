// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivDownloadCallbacks internal constructor(
    @JsonIgnore val onFailActions: Property<List<DivAction>>?,
    @JsonIgnore val onSuccessActions: Property<List<DivAction>>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "on_fail_actions" to onFailActions,
            "on_success_actions" to onSuccessActions,
        )
    }
}

fun <T> TemplateContext<T>.divDownloadCallbacks(): LiteralProperty<DivDownloadCallbacks> {
    return value(DivDownloadCallbacks(
        onFailActions = null,
        onSuccessActions = null,
    ))
}

fun <T> TemplateContext<T>.divDownloadCallbacks(
    onFailActions: Property<List<DivAction>>? = null,
    onSuccessActions: Property<List<DivAction>>? = null,
): LiteralProperty<DivDownloadCallbacks> {
    return value(DivDownloadCallbacks(
        onFailActions = onFailActions,
        onSuccessActions = onSuccessActions,
    ))
}

fun <T> TemplateContext<T>.divDownloadCallbacks(
    onFailActions: List<DivAction>? = null,
    onSuccessActions: List<DivAction>? = null,
): LiteralProperty<DivDownloadCallbacks> {
    return value(DivDownloadCallbacks(
        onFailActions = optionalValue(onFailActions),
        onSuccessActions = optionalValue(onSuccessActions),
    ))
}

fun CardContext.divDownloadCallbacks(): DivDownloadCallbacks {
    return DivDownloadCallbacks(
        onFailActions = null,
        onSuccessActions = null,
    )
}

fun CardContext.divDownloadCallbacks(
    onFailActions: ValueProperty<List<DivAction>>? = null,
    onSuccessActions: ValueProperty<List<DivAction>>? = null,
): DivDownloadCallbacks {
    return DivDownloadCallbacks(
        onFailActions = onFailActions,
        onSuccessActions = onSuccessActions,
    )
}

fun CardContext.divDownloadCallbacks(
    onFailActions: List<DivAction>? = null,
    onSuccessActions: List<DivAction>? = null,
): DivDownloadCallbacks {
    return DivDownloadCallbacks(
        onFailActions = optionalValue(onFailActions),
        onSuccessActions = optionalValue(onSuccessActions),
    )
}
