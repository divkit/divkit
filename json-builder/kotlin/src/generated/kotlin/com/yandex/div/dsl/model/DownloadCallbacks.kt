// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DownloadCallbacks internal constructor(
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

fun <T> TemplateContext<T>.downloadCallbacks(): LiteralProperty<DownloadCallbacks> {
    return value(DownloadCallbacks(
        onFailActions = null,
        onSuccessActions = null,
    ))
}

fun <T> TemplateContext<T>.downloadCallbacks(
    onFailActions: Property<List<DivAction>>? = null,
    onSuccessActions: Property<List<DivAction>>? = null,
): LiteralProperty<DownloadCallbacks> {
    return value(DownloadCallbacks(
        onFailActions = onFailActions,
        onSuccessActions = onSuccessActions,
    ))
}

fun <T> TemplateContext<T>.downloadCallbacks(
    onFailActions: List<DivAction>? = null,
    onSuccessActions: List<DivAction>? = null,
): LiteralProperty<DownloadCallbacks> {
    return value(DownloadCallbacks(
        onFailActions = optionalValue(onFailActions),
        onSuccessActions = optionalValue(onSuccessActions),
    ))
}

fun CardContext.downloadCallbacks(): DownloadCallbacks {
    return DownloadCallbacks(
        onFailActions = null,
        onSuccessActions = null,
    )
}

fun CardContext.downloadCallbacks(
    onFailActions: ValueProperty<List<DivAction>>? = null,
    onSuccessActions: ValueProperty<List<DivAction>>? = null,
): DownloadCallbacks {
    return DownloadCallbacks(
        onFailActions = onFailActions,
        onSuccessActions = onSuccessActions,
    )
}

fun CardContext.downloadCallbacks(
    onFailActions: List<DivAction>? = null,
    onSuccessActions: List<DivAction>? = null,
): DownloadCallbacks {
    return DownloadCallbacks(
        onFailActions = optionalValue(onFailActions),
        onSuccessActions = optionalValue(onSuccessActions),
    )
}
