@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * Actions performed when an element is no longer visible.
 * 
 * Can be created using the method [disappearAction].
 * 
 * Required parameters: `log_id`.
 */
@Generated
class DisappearAction internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): DisappearAction = DisappearAction(
        Properties(
            disappearDuration = additive.disappearDuration ?: properties.disappearDuration,
            downloadCallbacks = additive.downloadCallbacks ?: properties.downloadCallbacks,
            isEnabled = additive.isEnabled ?: properties.isEnabled,
            logId = additive.logId ?: properties.logId,
            logLimit = additive.logLimit ?: properties.logLimit,
            payload = additive.payload ?: properties.payload,
            referer = additive.referer ?: properties.referer,
            typed = additive.typed ?: properties.typed,
            url = additive.url ?: properties.url,
            visibilityPercentage = additive.visibilityPercentage ?: properties.visibilityPercentage,
        )
    )

    class Properties internal constructor(
        /**
         * Time in milliseconds during which an element must be outside the visible area to trigger `disappear-action`.
         * Default value: `800`.
         */
        val disappearDuration: Property<Int>?,
        /**
         * Callbacks that are called after [data loading](../../interaction#loading-data).
         */
        val downloadCallbacks: Property<DownloadCallbacks>?,
        /**
         * The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
         * Default value: `true`.
         */
        val isEnabled: Property<Boolean>?,
        /**
         * Logging ID.
         */
        val logId: Property<String>?,
        /**
         * Limit on the number of loggings. If `0`, the limit is removed.
         * Default value: `1`.
         */
        val logLimit: Property<Int>?,
        /**
         * Additional parameters, passed to the host application.
         */
        val payload: Property<Map<String, Any>>?,
        /**
         * Referer URL for logging.
         */
        val referer: Property<Url>?,
        val typed: Property<ActionTyped>?,
        /**
         * URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction).
         */
        val url: Property<Url>?,
        /**
         * Percentage of the visible part of an element that triggers `disappear-action`.
         * Default value: `0`.
         */
        val visibilityPercentage: Property<Int>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("disappear_duration", disappearDuration)
            result.tryPutProperty("download_callbacks", downloadCallbacks)
            result.tryPutProperty("is_enabled", isEnabled)
            result.tryPutProperty("log_id", logId)
            result.tryPutProperty("log_limit", logLimit)
            result.tryPutProperty("payload", payload)
            result.tryPutProperty("referer", referer)
            result.tryPutProperty("typed", typed)
            result.tryPutProperty("url", url)
            result.tryPutProperty("visibility_percentage", visibilityPercentage)
            return result
        }
    }
}

/**
 * @param disappearDuration Time in milliseconds during which an element must be outside the visible area to trigger `disappear-action`.
 * @param downloadCallbacks Callbacks that are called after [data loading](../../interaction#loading-data).
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logLimit Limit on the number of loggings. If `0`, the limit is removed.
 * @param payload Additional parameters, passed to the host application.
 * @param referer Referer URL for logging.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction).
 * @param visibilityPercentage Percentage of the visible part of an element that triggers `disappear-action`.
 */
@Generated
fun DivScope.disappearAction(
    `use named arguments`: Guard = Guard.instance,
    disappearDuration: Int? = null,
    downloadCallbacks: DownloadCallbacks? = null,
    isEnabled: Boolean? = null,
    logId: String? = null,
    logLimit: Int? = null,
    payload: Map<String, Any>? = null,
    referer: Url? = null,
    typed: ActionTyped? = null,
    url: Url? = null,
    visibilityPercentage: Int? = null,
): DisappearAction = DisappearAction(
    DisappearAction.Properties(
        disappearDuration = valueOrNull(disappearDuration),
        downloadCallbacks = valueOrNull(downloadCallbacks),
        isEnabled = valueOrNull(isEnabled),
        logId = valueOrNull(logId),
        logLimit = valueOrNull(logLimit),
        payload = valueOrNull(payload),
        referer = valueOrNull(referer),
        typed = valueOrNull(typed),
        url = valueOrNull(url),
        visibilityPercentage = valueOrNull(visibilityPercentage),
    )
)

/**
 * @param disappearDuration Time in milliseconds during which an element must be outside the visible area to trigger `disappear-action`.
 * @param downloadCallbacks Callbacks that are called after [data loading](../../interaction#loading-data).
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logLimit Limit on the number of loggings. If `0`, the limit is removed.
 * @param payload Additional parameters, passed to the host application.
 * @param referer Referer URL for logging.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction).
 * @param visibilityPercentage Percentage of the visible part of an element that triggers `disappear-action`.
 */
@Generated
fun DivScope.disappearActionProps(
    `use named arguments`: Guard = Guard.instance,
    disappearDuration: Int? = null,
    downloadCallbacks: DownloadCallbacks? = null,
    isEnabled: Boolean? = null,
    logId: String? = null,
    logLimit: Int? = null,
    payload: Map<String, Any>? = null,
    referer: Url? = null,
    typed: ActionTyped? = null,
    url: Url? = null,
    visibilityPercentage: Int? = null,
) = DisappearAction.Properties(
    disappearDuration = valueOrNull(disappearDuration),
    downloadCallbacks = valueOrNull(downloadCallbacks),
    isEnabled = valueOrNull(isEnabled),
    logId = valueOrNull(logId),
    logLimit = valueOrNull(logLimit),
    payload = valueOrNull(payload),
    referer = valueOrNull(referer),
    typed = valueOrNull(typed),
    url = valueOrNull(url),
    visibilityPercentage = valueOrNull(visibilityPercentage),
)

/**
 * @param disappearDuration Time in milliseconds during which an element must be outside the visible area to trigger `disappear-action`.
 * @param downloadCallbacks Callbacks that are called after [data loading](../../interaction#loading-data).
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logLimit Limit on the number of loggings. If `0`, the limit is removed.
 * @param payload Additional parameters, passed to the host application.
 * @param referer Referer URL for logging.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction).
 * @param visibilityPercentage Percentage of the visible part of an element that triggers `disappear-action`.
 */
@Generated
fun TemplateScope.disappearActionRefs(
    `use named arguments`: Guard = Guard.instance,
    disappearDuration: ReferenceProperty<Int>? = null,
    downloadCallbacks: ReferenceProperty<DownloadCallbacks>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
    logId: ReferenceProperty<String>? = null,
    logLimit: ReferenceProperty<Int>? = null,
    payload: ReferenceProperty<Map<String, Any>>? = null,
    referer: ReferenceProperty<Url>? = null,
    typed: ReferenceProperty<ActionTyped>? = null,
    url: ReferenceProperty<Url>? = null,
    visibilityPercentage: ReferenceProperty<Int>? = null,
) = DisappearAction.Properties(
    disappearDuration = disappearDuration,
    downloadCallbacks = downloadCallbacks,
    isEnabled = isEnabled,
    logId = logId,
    logLimit = logLimit,
    payload = payload,
    referer = referer,
    typed = typed,
    url = url,
    visibilityPercentage = visibilityPercentage,
)

/**
 * @param disappearDuration Time in milliseconds during which an element must be outside the visible area to trigger `disappear-action`.
 * @param downloadCallbacks Callbacks that are called after [data loading](../../interaction#loading-data).
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logLimit Limit on the number of loggings. If `0`, the limit is removed.
 * @param payload Additional parameters, passed to the host application.
 * @param referer Referer URL for logging.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction).
 * @param visibilityPercentage Percentage of the visible part of an element that triggers `disappear-action`.
 */
@Generated
fun DisappearAction.override(
    `use named arguments`: Guard = Guard.instance,
    disappearDuration: Int? = null,
    downloadCallbacks: DownloadCallbacks? = null,
    isEnabled: Boolean? = null,
    logId: String? = null,
    logLimit: Int? = null,
    payload: Map<String, Any>? = null,
    referer: Url? = null,
    typed: ActionTyped? = null,
    url: Url? = null,
    visibilityPercentage: Int? = null,
): DisappearAction = DisappearAction(
    DisappearAction.Properties(
        disappearDuration = valueOrNull(disappearDuration) ?: properties.disappearDuration,
        downloadCallbacks = valueOrNull(downloadCallbacks) ?: properties.downloadCallbacks,
        isEnabled = valueOrNull(isEnabled) ?: properties.isEnabled,
        logId = valueOrNull(logId) ?: properties.logId,
        logLimit = valueOrNull(logLimit) ?: properties.logLimit,
        payload = valueOrNull(payload) ?: properties.payload,
        referer = valueOrNull(referer) ?: properties.referer,
        typed = valueOrNull(typed) ?: properties.typed,
        url = valueOrNull(url) ?: properties.url,
        visibilityPercentage = valueOrNull(visibilityPercentage) ?: properties.visibilityPercentage,
    )
)

/**
 * @param disappearDuration Time in milliseconds during which an element must be outside the visible area to trigger `disappear-action`.
 * @param downloadCallbacks Callbacks that are called after [data loading](../../interaction#loading-data).
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logLimit Limit on the number of loggings. If `0`, the limit is removed.
 * @param payload Additional parameters, passed to the host application.
 * @param referer Referer URL for logging.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction).
 * @param visibilityPercentage Percentage of the visible part of an element that triggers `disappear-action`.
 */
@Generated
fun DisappearAction.defer(
    `use named arguments`: Guard = Guard.instance,
    disappearDuration: ReferenceProperty<Int>? = null,
    downloadCallbacks: ReferenceProperty<DownloadCallbacks>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
    logId: ReferenceProperty<String>? = null,
    logLimit: ReferenceProperty<Int>? = null,
    payload: ReferenceProperty<Map<String, Any>>? = null,
    referer: ReferenceProperty<Url>? = null,
    typed: ReferenceProperty<ActionTyped>? = null,
    url: ReferenceProperty<Url>? = null,
    visibilityPercentage: ReferenceProperty<Int>? = null,
): DisappearAction = DisappearAction(
    DisappearAction.Properties(
        disappearDuration = disappearDuration ?: properties.disappearDuration,
        downloadCallbacks = downloadCallbacks ?: properties.downloadCallbacks,
        isEnabled = isEnabled ?: properties.isEnabled,
        logId = logId ?: properties.logId,
        logLimit = logLimit ?: properties.logLimit,
        payload = payload ?: properties.payload,
        referer = referer ?: properties.referer,
        typed = typed ?: properties.typed,
        url = url ?: properties.url,
        visibilityPercentage = visibilityPercentage ?: properties.visibilityPercentage,
    )
)

/**
 * @param disappearDuration Time in milliseconds during which an element must be outside the visible area to trigger `disappear-action`.
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logLimit Limit on the number of loggings. If `0`, the limit is removed.
 * @param referer Referer URL for logging.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction).
 * @param visibilityPercentage Percentage of the visible part of an element that triggers `disappear-action`.
 */
@Generated
fun DisappearAction.evaluate(
    `use named arguments`: Guard = Guard.instance,
    disappearDuration: ExpressionProperty<Int>? = null,
    isEnabled: ExpressionProperty<Boolean>? = null,
    logLimit: ExpressionProperty<Int>? = null,
    referer: ExpressionProperty<Url>? = null,
    url: ExpressionProperty<Url>? = null,
    visibilityPercentage: ExpressionProperty<Int>? = null,
): DisappearAction = DisappearAction(
    DisappearAction.Properties(
        disappearDuration = disappearDuration ?: properties.disappearDuration,
        downloadCallbacks = properties.downloadCallbacks,
        isEnabled = isEnabled ?: properties.isEnabled,
        logId = properties.logId,
        logLimit = logLimit ?: properties.logLimit,
        payload = properties.payload,
        referer = referer ?: properties.referer,
        typed = properties.typed,
        url = url ?: properties.url,
        visibilityPercentage = visibilityPercentage ?: properties.visibilityPercentage,
    )
)

@Generated
fun DisappearAction.asList() = listOf(this)
