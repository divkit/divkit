// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivVisibilityAction internal constructor(
    @JsonIgnore val downloadCallbacks: Property<DownloadCallbacks>?,
    @JsonIgnore val logId: Property<String>?,
    @JsonIgnore val logLimit: Property<Int>?,
    @JsonIgnore val payload: Property<Map<String, Any>>?,
    @JsonIgnore val referer: Property<URI>?,
    @JsonIgnore val url: Property<URI>?,
    @JsonIgnore val visibilityDuration: Property<Int>?,
    @JsonIgnore val visibilityPercentage: Property<Int>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "download_callbacks" to downloadCallbacks,
            "log_id" to logId,
            "log_limit" to logLimit,
            "payload" to payload,
            "referer" to referer,
            "url" to url,
            "visibility_duration" to visibilityDuration,
            "visibility_percentage" to visibilityPercentage,
        )
    }
}

fun <T> TemplateContext<T>.divVisibilityAction(): LiteralProperty<DivVisibilityAction> {
    return value(DivVisibilityAction(
        downloadCallbacks = null,
        logId = null,
        logLimit = null,
        payload = null,
        referer = null,
        url = null,
        visibilityDuration = null,
        visibilityPercentage = null,
    ))
}

fun <T> TemplateContext<T>.divVisibilityAction(
    logId: Property<String>? = null,
    downloadCallbacks: Property<DownloadCallbacks>? = null,
    logLimit: Property<Int>? = null,
    payload: Property<Map<String, Any>>? = null,
    referer: Property<URI>? = null,
    url: Property<URI>? = null,
    visibilityDuration: Property<Int>? = null,
    visibilityPercentage: Property<Int>? = null,
): LiteralProperty<DivVisibilityAction> {
    return value(DivVisibilityAction(
        downloadCallbacks = downloadCallbacks,
        logId = logId,
        logLimit = logLimit,
        payload = payload,
        referer = referer,
        url = url,
        visibilityDuration = visibilityDuration,
        visibilityPercentage = visibilityPercentage,
    ))
}

fun <T> TemplateContext<T>.divVisibilityAction(
    logId: String? = null,
    downloadCallbacks: DownloadCallbacks? = null,
    logLimit: Int? = null,
    payload: Map<String, Any>? = null,
    referer: URI? = null,
    url: URI? = null,
    visibilityDuration: Int? = null,
    visibilityPercentage: Int? = null,
): LiteralProperty<DivVisibilityAction> {
    return value(DivVisibilityAction(
        downloadCallbacks = optionalValue(downloadCallbacks),
        logId = optionalValue(logId),
        logLimit = optionalValue(logLimit),
        payload = optionalValue(payload),
        referer = optionalValue(referer),
        url = optionalValue(url),
        visibilityDuration = optionalValue(visibilityDuration),
        visibilityPercentage = optionalValue(visibilityPercentage),
    ))
}

fun CardContext.divVisibilityAction(
    logId: ValueProperty<String>,
    downloadCallbacks: ValueProperty<DownloadCallbacks>? = null,
    logLimit: ValueProperty<Int>? = null,
    payload: ValueProperty<Map<String, Any>>? = null,
    referer: ValueProperty<URI>? = null,
    url: ValueProperty<URI>? = null,
    visibilityDuration: ValueProperty<Int>? = null,
    visibilityPercentage: ValueProperty<Int>? = null,
): DivVisibilityAction {
    return DivVisibilityAction(
        downloadCallbacks = downloadCallbacks,
        logId = logId,
        logLimit = logLimit,
        payload = payload,
        referer = referer,
        url = url,
        visibilityDuration = visibilityDuration,
        visibilityPercentage = visibilityPercentage,
    )
}

fun CardContext.divVisibilityAction(
    logId: String,
    downloadCallbacks: DownloadCallbacks? = null,
    logLimit: Int? = null,
    payload: Map<String, Any>? = null,
    referer: URI? = null,
    url: URI? = null,
    visibilityDuration: Int? = null,
    visibilityPercentage: Int? = null,
): DivVisibilityAction {
    return DivVisibilityAction(
        downloadCallbacks = optionalValue(downloadCallbacks),
        logId = value(logId),
        logLimit = optionalValue(logLimit),
        payload = optionalValue(payload),
        referer = optionalValue(referer),
        url = optionalValue(url),
        visibilityDuration = optionalValue(visibilityDuration),
        visibilityPercentage = optionalValue(visibilityPercentage),
    )
}
