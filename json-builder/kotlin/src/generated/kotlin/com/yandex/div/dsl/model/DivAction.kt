// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivAction internal constructor(
    @JsonIgnore val downloadCallbacks: Property<DownloadCallbacks>?,
    @JsonIgnore val hover: Property<DivHover>?,
    @JsonIgnore val logId: Property<String>?,
    @JsonIgnore val logUrl: Property<URI>?,
    @JsonIgnore val menuItems: Property<List<MenuItem>>?,
    @JsonIgnore val payload: Property<Map<String, Any>>?,
    @JsonIgnore val referer: Property<URI>?,
    @JsonIgnore val target: Property<Target>?,
    @JsonIgnore val url: Property<URI>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "download_callbacks" to downloadCallbacks,
            "hover" to hover,
            "log_id" to logId,
            "log_url" to logUrl,
            "menu_items" to menuItems,
            "payload" to payload,
            "referer" to referer,
            "target" to target,
            "url" to url,
        )
    }

    enum class Target(@JsonValue val value: String) {
        SELF("_self"),
        BLANK("_blank"),
    }

    class MenuItem internal constructor(
        @JsonIgnore val action: Property<DivAction>?,
        @JsonIgnore val actions: Property<List<DivAction>>?,
        @JsonIgnore val text: Property<String>?,
    ) {

        @JsonAnyGetter
        internal fun properties(): Map<String, Any> {
            return propertyMapOf(
                "action" to action,
                "actions" to actions,
                "text" to text,
            )
        }
    }
}

fun <T> TemplateContext<T>.divAction(): LiteralProperty<DivAction> {
    return value(DivAction(
        downloadCallbacks = null,
        hover = null,
        logId = null,
        logUrl = null,
        menuItems = null,
        payload = null,
        referer = null,
        target = null,
        url = null,
    ))
}

fun <T> TemplateContext<T>.divAction(
    logId: Property<String>? = null,
    downloadCallbacks: Property<DownloadCallbacks>? = null,
    hover: Property<DivHover>? = null,
    logUrl: Property<URI>? = null,
    menuItems: Property<List<DivAction.MenuItem>>? = null,
    payload: Property<Map<String, Any>>? = null,
    referer: Property<URI>? = null,
    target: Property<DivAction.Target>? = null,
    url: Property<URI>? = null,
): LiteralProperty<DivAction> {
    return value(DivAction(
        downloadCallbacks = downloadCallbacks,
        hover = hover,
        logId = logId,
        logUrl = logUrl,
        menuItems = menuItems,
        payload = payload,
        referer = referer,
        target = target,
        url = url,
    ))
}

fun <T> TemplateContext<T>.divAction(
    logId: String? = null,
    downloadCallbacks: DownloadCallbacks? = null,
    hover: DivHover? = null,
    logUrl: URI? = null,
    menuItems: List<DivAction.MenuItem>? = null,
    payload: Map<String, Any>? = null,
    referer: URI? = null,
    target: DivAction.Target? = null,
    url: URI? = null,
): LiteralProperty<DivAction> {
    return value(DivAction(
        downloadCallbacks = optionalValue(downloadCallbacks),
        hover = optionalValue(hover),
        logId = optionalValue(logId),
        logUrl = optionalValue(logUrl),
        menuItems = optionalValue(menuItems),
        payload = optionalValue(payload),
        referer = optionalValue(referer),
        target = optionalValue(target),
        url = optionalValue(url),
    ))
}

fun <T> TemplateContext<T>.menuItem(): LiteralProperty<DivAction.MenuItem> {
    return value(DivAction.MenuItem(
        action = null,
        actions = null,
        text = null,
    ))
}

fun <T> TemplateContext<T>.menuItem(
    text: Property<String>? = null,
    action: Property<DivAction>? = null,
    actions: Property<List<DivAction>>? = null,
): LiteralProperty<DivAction.MenuItem> {
    return value(DivAction.MenuItem(
        action = action,
        actions = actions,
        text = text,
    ))
}

fun <T> TemplateContext<T>.menuItem(
    text: String? = null,
    action: DivAction? = null,
    actions: List<DivAction>? = null,
): LiteralProperty<DivAction.MenuItem> {
    return value(DivAction.MenuItem(
        action = optionalValue(action),
        actions = optionalValue(actions),
        text = optionalValue(text),
    ))
}

fun CardContext.divAction(
    logId: ValueProperty<String>,
    downloadCallbacks: ValueProperty<DownloadCallbacks>? = null,
    hover: ValueProperty<DivHover>? = null,
    logUrl: ValueProperty<URI>? = null,
    menuItems: ValueProperty<List<DivAction.MenuItem>>? = null,
    payload: ValueProperty<Map<String, Any>>? = null,
    referer: ValueProperty<URI>? = null,
    target: ValueProperty<DivAction.Target>? = null,
    url: ValueProperty<URI>? = null,
): DivAction {
    return DivAction(
        downloadCallbacks = downloadCallbacks,
        hover = hover,
        logId = logId,
        logUrl = logUrl,
        menuItems = menuItems,
        payload = payload,
        referer = referer,
        target = target,
        url = url,
    )
}

fun CardContext.divAction(
    logId: String,
    downloadCallbacks: DownloadCallbacks? = null,
    hover: DivHover? = null,
    logUrl: URI? = null,
    menuItems: List<DivAction.MenuItem>? = null,
    payload: Map<String, Any>? = null,
    referer: URI? = null,
    target: DivAction.Target? = null,
    url: URI? = null,
): DivAction {
    return DivAction(
        downloadCallbacks = optionalValue(downloadCallbacks),
        hover = optionalValue(hover),
        logId = value(logId),
        logUrl = optionalValue(logUrl),
        menuItems = optionalValue(menuItems),
        payload = optionalValue(payload),
        referer = optionalValue(referer),
        target = optionalValue(target),
        url = optionalValue(url),
    )
}

fun CardContext.menuItem(
    text: ValueProperty<String>,
    action: ValueProperty<DivAction>? = null,
    actions: ValueProperty<List<DivAction>>? = null,
): DivAction.MenuItem {
    return DivAction.MenuItem(
        action = action,
        actions = actions,
        text = text,
    )
}

fun CardContext.menuItem(
    text: String,
    action: DivAction? = null,
    actions: List<DivAction>? = null,
): DivAction.MenuItem {
    return DivAction.MenuItem(
        action = optionalValue(action),
        actions = optionalValue(actions),
        text = value(text),
    )
}
