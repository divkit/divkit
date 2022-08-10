// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class ActionBase internal constructor(
    @JsonIgnore val logId: Property<String>?,
    @JsonIgnore val payload: Property<Map<String, Any>>?,
    @JsonIgnore val referer: Property<URI>?,
    @JsonIgnore val url: Property<URI>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "log_id" to logId,
            "payload" to payload,
            "referer" to referer,
            "url" to url,
        )
    }
}

fun <T> TemplateContext<T>.actionBase(): LiteralProperty<ActionBase> {
    return value(ActionBase(
        logId = null,
        payload = null,
        referer = null,
        url = null,
    ))
}

fun <T> TemplateContext<T>.actionBase(
    logId: Property<String>? = null,
    payload: Property<Map<String, Any>>? = null,
    referer: Property<URI>? = null,
    url: Property<URI>? = null,
): LiteralProperty<ActionBase> {
    return value(ActionBase(
        logId = logId,
        payload = payload,
        referer = referer,
        url = url,
    ))
}

fun <T> TemplateContext<T>.actionBase(
    logId: String? = null,
    payload: Map<String, Any>? = null,
    referer: URI? = null,
    url: URI? = null,
): LiteralProperty<ActionBase> {
    return value(ActionBase(
        logId = optionalValue(logId),
        payload = optionalValue(payload),
        referer = optionalValue(referer),
        url = optionalValue(url),
    ))
}

fun CardContext.actionBase(): ActionBase {
    return ActionBase(
        logId = null,
        payload = null,
        referer = null,
        url = null,
    )
}

fun CardContext.actionBase(
    logId: ValueProperty<String>? = null,
    payload: ValueProperty<Map<String, Any>>? = null,
    referer: ValueProperty<URI>? = null,
    url: ValueProperty<URI>? = null,
): ActionBase {
    return ActionBase(
        logId = logId,
        payload = payload,
        referer = referer,
        url = url,
    )
}

fun CardContext.actionBase(
    logId: String? = null,
    payload: Map<String, Any>? = null,
    referer: URI? = null,
    url: URI? = null,
): ActionBase {
    return ActionBase(
        logId = optionalValue(logId),
        payload = optionalValue(payload),
        referer = optionalValue(referer),
        url = optionalValue(url),
    )
}
