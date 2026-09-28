package com.yandex.div.compose.actions

import android.net.Uri
import com.yandex.div2.DivActionTyped
import org.json.JSONObject

/**
 * An enabled action as observed before it is handled.
 *
 * [id], [url], [logUrl] and [referer] are evaluated in the triggering element's scope.
 * [typed] takes precedence over [url]; [url] is null for typed actions.
 * The typed model's expressions can be evaluated with the accompanying action context.
 * [logUrl] is an optional HTTP(S) tap beacon URL, provided for taps, double taps, long taps,
 * menu item clicks and text links, matching the Android View renderer's tap beacon handling.
 * When a gesture opens a menu, only the first menu action provides [logUrl].
 * It is null for other dispatches, including tab title clicks and taps outside tooltips,
 * even when their [source] is [DivActionSource.TAP].
 * DivKit does not send it automatically; the application can send it from [DivActionLogger],
 * using [referer] when present.
 */
@ConsistentCopyVisibility
data class DivActionEvent internal constructor(
    val id: String?,
    val payload: JSONObject?,
    val source: DivActionSource,
    val url: Uri?,
    val typed: DivActionTyped?,
    val logUrl: Uri?,
    val referer: Uri?,
)
