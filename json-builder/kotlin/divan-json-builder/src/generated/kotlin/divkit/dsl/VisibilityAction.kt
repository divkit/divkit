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
 * Actions performed when an element becomes visible.
 * 
 * Can be created using the method [visibilityAction].
 * 
 * Required parameters: `log_id`.
 */
@Generated
@ExposedCopyVisibility
data class VisibilityAction internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): VisibilityAction = VisibilityAction(
        Properties(
            downloadCallbacks = additive.downloadCallbacks ?: properties.downloadCallbacks,
            isEnabled = additive.isEnabled ?: properties.isEnabled,
            logId = additive.logId ?: properties.logId,
            logLimit = additive.logLimit ?: properties.logLimit,
            payload = additive.payload ?: properties.payload,
            referer = additive.referer ?: properties.referer,
            scopeId = additive.scopeId ?: properties.scopeId,
            typed = additive.typed ?: properties.typed,
            url = additive.url ?: properties.url,
            visibilityDuration = additive.visibilityDuration ?: properties.visibilityDuration,
            visibilityPercentage = additive.visibilityPercentage ?: properties.visibilityPercentage,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
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
        /**
         * The ID of the element within which the specified action will be performed.
         */
        val scopeId: Property<String>?,
        val typed: Property<ActionTyped>?,
        /**
         * URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction).
         */
        val url: Property<Url>?,
        /**
         * Time in milliseconds during which an element must be visible to trigger `visibility-action`.
         * Default value: `800`.
         */
        val visibilityDuration: Property<Int>?,
        /**
         * Percentage of the visible part of an element that triggers `visibility-action`.
         * Default value: `50`.
         */
        val visibilityPercentage: Property<Int>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("download_callbacks", downloadCallbacks)
            result.tryPutProperty("is_enabled", isEnabled)
            result.tryPutProperty("log_id", logId)
            result.tryPutProperty("log_limit", logLimit)
            result.tryPutProperty("payload", payload)
            result.tryPutProperty("referer", referer)
            result.tryPutProperty("scope_id", scopeId)
            result.tryPutProperty("typed", typed)
            result.tryPutProperty("url", url)
            result.tryPutProperty("visibility_duration", visibilityDuration)
            result.tryPutProperty("visibility_percentage", visibilityPercentage)
            return result
        }
    }
}

/**
 * @param downloadCallbacks Callbacks that are called after [data loading](../../interaction#loading-data).
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logLimit Limit on the number of loggings. If `0`, the limit is removed.
 * @param payload Additional parameters, passed to the host application.
 * @param referer Referer URL for logging.
 * @param scopeId The ID of the element within which the specified action will be performed.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction).
 * @param visibilityDuration Time in milliseconds during which an element must be visible to trigger `visibility-action`.
 * @param visibilityPercentage Percentage of the visible part of an element that triggers `visibility-action`.
 */
@Generated
fun DivScope.visibilityAction(
    `use named arguments`: Guard = Guard.instance,
    downloadCallbacks: DownloadCallbacks? = null,
    isEnabled: Boolean? = null,
    logId: String? = null,
    logLimit: Int? = null,
    payload: Map<String, Any>? = null,
    referer: Url? = null,
    scopeId: String? = null,
    typed: ActionTyped? = null,
    url: Url? = null,
    visibilityDuration: Int? = null,
    visibilityPercentage: Int? = null,
): VisibilityAction = VisibilityAction(
    VisibilityAction.Properties(
        downloadCallbacks = valueOrNull(downloadCallbacks),
        isEnabled = valueOrNull(isEnabled),
        logId = valueOrNull(logId),
        logLimit = valueOrNull(logLimit),
        payload = valueOrNull(payload),
        referer = valueOrNull(referer),
        scopeId = valueOrNull(scopeId),
        typed = valueOrNull(typed),
        url = valueOrNull(url),
        visibilityDuration = valueOrNull(visibilityDuration),
        visibilityPercentage = valueOrNull(visibilityPercentage),
    )
)

/**
 * @param downloadCallbacks Callbacks that are called after [data loading](../../interaction#loading-data).
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logLimit Limit on the number of loggings. If `0`, the limit is removed.
 * @param payload Additional parameters, passed to the host application.
 * @param referer Referer URL for logging.
 * @param scopeId The ID of the element within which the specified action will be performed.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction).
 * @param visibilityDuration Time in milliseconds during which an element must be visible to trigger `visibility-action`.
 * @param visibilityPercentage Percentage of the visible part of an element that triggers `visibility-action`.
 */
@Generated
fun DivScope.visibilityActionProps(
    `use named arguments`: Guard = Guard.instance,
    downloadCallbacks: DownloadCallbacks? = null,
    isEnabled: Boolean? = null,
    logId: String? = null,
    logLimit: Int? = null,
    payload: Map<String, Any>? = null,
    referer: Url? = null,
    scopeId: String? = null,
    typed: ActionTyped? = null,
    url: Url? = null,
    visibilityDuration: Int? = null,
    visibilityPercentage: Int? = null,
) = VisibilityAction.Properties(
    downloadCallbacks = valueOrNull(downloadCallbacks),
    isEnabled = valueOrNull(isEnabled),
    logId = valueOrNull(logId),
    logLimit = valueOrNull(logLimit),
    payload = valueOrNull(payload),
    referer = valueOrNull(referer),
    scopeId = valueOrNull(scopeId),
    typed = valueOrNull(typed),
    url = valueOrNull(url),
    visibilityDuration = valueOrNull(visibilityDuration),
    visibilityPercentage = valueOrNull(visibilityPercentage),
)

/**
 * @param downloadCallbacks Callbacks that are called after [data loading](../../interaction#loading-data).
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logLimit Limit on the number of loggings. If `0`, the limit is removed.
 * @param payload Additional parameters, passed to the host application.
 * @param referer Referer URL for logging.
 * @param scopeId The ID of the element within which the specified action will be performed.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction).
 * @param visibilityDuration Time in milliseconds during which an element must be visible to trigger `visibility-action`.
 * @param visibilityPercentage Percentage of the visible part of an element that triggers `visibility-action`.
 */
@Generated
fun TemplateScope.visibilityActionRefs(
    `use named arguments`: Guard = Guard.instance,
    downloadCallbacks: ReferenceProperty<DownloadCallbacks>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
    logId: ReferenceProperty<String>? = null,
    logLimit: ReferenceProperty<Int>? = null,
    payload: ReferenceProperty<Map<String, Any>>? = null,
    referer: ReferenceProperty<Url>? = null,
    scopeId: ReferenceProperty<String>? = null,
    typed: ReferenceProperty<ActionTyped>? = null,
    url: ReferenceProperty<Url>? = null,
    visibilityDuration: ReferenceProperty<Int>? = null,
    visibilityPercentage: ReferenceProperty<Int>? = null,
) = VisibilityAction.Properties(
    downloadCallbacks = downloadCallbacks,
    isEnabled = isEnabled,
    logId = logId,
    logLimit = logLimit,
    payload = payload,
    referer = referer,
    scopeId = scopeId,
    typed = typed,
    url = url,
    visibilityDuration = visibilityDuration,
    visibilityPercentage = visibilityPercentage,
)

/**
 * @param downloadCallbacks Callbacks that are called after [data loading](../../interaction#loading-data).
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logLimit Limit on the number of loggings. If `0`, the limit is removed.
 * @param payload Additional parameters, passed to the host application.
 * @param referer Referer URL for logging.
 * @param scopeId The ID of the element within which the specified action will be performed.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction).
 * @param visibilityDuration Time in milliseconds during which an element must be visible to trigger `visibility-action`.
 * @param visibilityPercentage Percentage of the visible part of an element that triggers `visibility-action`.
 */
@Generated
fun VisibilityAction.override(
    `use named arguments`: Guard = Guard.instance,
    downloadCallbacks: DownloadCallbacks? = null,
    isEnabled: Boolean? = null,
    logId: String? = null,
    logLimit: Int? = null,
    payload: Map<String, Any>? = null,
    referer: Url? = null,
    scopeId: String? = null,
    typed: ActionTyped? = null,
    url: Url? = null,
    visibilityDuration: Int? = null,
    visibilityPercentage: Int? = null,
): VisibilityAction = VisibilityAction(
    VisibilityAction.Properties(
        downloadCallbacks = valueOrNull(downloadCallbacks) ?: properties.downloadCallbacks,
        isEnabled = valueOrNull(isEnabled) ?: properties.isEnabled,
        logId = valueOrNull(logId) ?: properties.logId,
        logLimit = valueOrNull(logLimit) ?: properties.logLimit,
        payload = valueOrNull(payload) ?: properties.payload,
        referer = valueOrNull(referer) ?: properties.referer,
        scopeId = valueOrNull(scopeId) ?: properties.scopeId,
        typed = valueOrNull(typed) ?: properties.typed,
        url = valueOrNull(url) ?: properties.url,
        visibilityDuration = valueOrNull(visibilityDuration) ?: properties.visibilityDuration,
        visibilityPercentage = valueOrNull(visibilityPercentage) ?: properties.visibilityPercentage,
    )
)

/**
 * @param downloadCallbacks Callbacks that are called after [data loading](../../interaction#loading-data).
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logLimit Limit on the number of loggings. If `0`, the limit is removed.
 * @param payload Additional parameters, passed to the host application.
 * @param referer Referer URL for logging.
 * @param scopeId The ID of the element within which the specified action will be performed.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction).
 * @param visibilityDuration Time in milliseconds during which an element must be visible to trigger `visibility-action`.
 * @param visibilityPercentage Percentage of the visible part of an element that triggers `visibility-action`.
 */
@Generated
fun VisibilityAction.defer(
    `use named arguments`: Guard = Guard.instance,
    downloadCallbacks: ReferenceProperty<DownloadCallbacks>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
    logId: ReferenceProperty<String>? = null,
    logLimit: ReferenceProperty<Int>? = null,
    payload: ReferenceProperty<Map<String, Any>>? = null,
    referer: ReferenceProperty<Url>? = null,
    scopeId: ReferenceProperty<String>? = null,
    typed: ReferenceProperty<ActionTyped>? = null,
    url: ReferenceProperty<Url>? = null,
    visibilityDuration: ReferenceProperty<Int>? = null,
    visibilityPercentage: ReferenceProperty<Int>? = null,
): VisibilityAction = VisibilityAction(
    VisibilityAction.Properties(
        downloadCallbacks = downloadCallbacks ?: properties.downloadCallbacks,
        isEnabled = isEnabled ?: properties.isEnabled,
        logId = logId ?: properties.logId,
        logLimit = logLimit ?: properties.logLimit,
        payload = payload ?: properties.payload,
        referer = referer ?: properties.referer,
        scopeId = scopeId ?: properties.scopeId,
        typed = typed ?: properties.typed,
        url = url ?: properties.url,
        visibilityDuration = visibilityDuration ?: properties.visibilityDuration,
        visibilityPercentage = visibilityPercentage ?: properties.visibilityPercentage,
    )
)

/**
 * @param downloadCallbacks Callbacks that are called after [data loading](../../interaction#loading-data).
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logLimit Limit on the number of loggings. If `0`, the limit is removed.
 * @param payload Additional parameters, passed to the host application.
 * @param referer Referer URL for logging.
 * @param scopeId The ID of the element within which the specified action will be performed.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction).
 * @param visibilityDuration Time in milliseconds during which an element must be visible to trigger `visibility-action`.
 * @param visibilityPercentage Percentage of the visible part of an element that triggers `visibility-action`.
 */
@Generated
fun VisibilityAction.modify(
    `use named arguments`: Guard = Guard.instance,
    downloadCallbacks: Property<DownloadCallbacks>? = null,
    isEnabled: Property<Boolean>? = null,
    logId: Property<String>? = null,
    logLimit: Property<Int>? = null,
    payload: Property<Map<String, Any>>? = null,
    referer: Property<Url>? = null,
    scopeId: Property<String>? = null,
    typed: Property<ActionTyped>? = null,
    url: Property<Url>? = null,
    visibilityDuration: Property<Int>? = null,
    visibilityPercentage: Property<Int>? = null,
): VisibilityAction = VisibilityAction(
    VisibilityAction.Properties(
        downloadCallbacks = downloadCallbacks ?: properties.downloadCallbacks,
        isEnabled = isEnabled ?: properties.isEnabled,
        logId = logId ?: properties.logId,
        logLimit = logLimit ?: properties.logLimit,
        payload = payload ?: properties.payload,
        referer = referer ?: properties.referer,
        scopeId = scopeId ?: properties.scopeId,
        typed = typed ?: properties.typed,
        url = url ?: properties.url,
        visibilityDuration = visibilityDuration ?: properties.visibilityDuration,
        visibilityPercentage = visibilityPercentage ?: properties.visibilityPercentage,
    )
)

/**
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logLimit Limit on the number of loggings. If `0`, the limit is removed.
 * @param referer Referer URL for logging.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction).
 * @param visibilityDuration Time in milliseconds during which an element must be visible to trigger `visibility-action`.
 * @param visibilityPercentage Percentage of the visible part of an element that triggers `visibility-action`.
 */
@Generated
fun VisibilityAction.evaluate(
    `use named arguments`: Guard = Guard.instance,
    isEnabled: ExpressionProperty<Boolean>? = null,
    logId: ExpressionProperty<String>? = null,
    logLimit: ExpressionProperty<Int>? = null,
    referer: ExpressionProperty<Url>? = null,
    url: ExpressionProperty<Url>? = null,
    visibilityDuration: ExpressionProperty<Int>? = null,
    visibilityPercentage: ExpressionProperty<Int>? = null,
): VisibilityAction = VisibilityAction(
    VisibilityAction.Properties(
        downloadCallbacks = properties.downloadCallbacks,
        isEnabled = isEnabled ?: properties.isEnabled,
        logId = logId ?: properties.logId,
        logLimit = logLimit ?: properties.logLimit,
        payload = properties.payload,
        referer = referer ?: properties.referer,
        scopeId = properties.scopeId,
        typed = properties.typed,
        url = url ?: properties.url,
        visibilityDuration = visibilityDuration ?: properties.visibilityDuration,
        visibilityPercentage = visibilityPercentage ?: properties.visibilityPercentage,
    )
)

@Generated
fun VisibilityAction.asList() = listOf(this)
