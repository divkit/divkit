@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import divkit.dsl.serialization.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * Callbacks that are called after [data loading](../../interaction#loading-data).
 * 
 * Can be created using the method [downloadCallbacks].
 */
@Generated
@JsonSerialize(using = DownloadCallbacksSerializer::class)
@ExposedCopyVisibility
data class DownloadCallbacks internal constructor(
    val properties: Properties,
) {
    operator fun plus(additive: Properties): DownloadCallbacks = DownloadCallbacks(
        Properties(
            onFailActions = additive.onFailActions ?: properties.onFailActions,
            onSuccessActions = additive.onSuccessActions ?: properties.onSuccessActions,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
         */
        val onFailActions: Property<List<Action>>?,
        /**
         * Actions in case of successful loading.
         */
        val onSuccessActions: Property<List<Action>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("on_fail_actions", onFailActions)
            result.tryPutProperty("on_success_actions", onSuccessActions)
            return result
        }
    }
}

/**
 * @param onFailActions Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
 * @param onSuccessActions Actions in case of successful loading.
 */
@Generated
fun DivScope.downloadCallbacks(
    `use named arguments`: Guard = Guard.instance,
    onFailActions: List<Action>? = null,
    onSuccessActions: List<Action>? = null,
): DownloadCallbacks = DownloadCallbacks(
    DownloadCallbacks.Properties(
        onFailActions = valueOrNull(onFailActions),
        onSuccessActions = valueOrNull(onSuccessActions),
    )
)

/**
 * @param onFailActions Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
 * @param onSuccessActions Actions in case of successful loading.
 */
@Generated
fun DivScope.downloadCallbacksProps(
    `use named arguments`: Guard = Guard.instance,
    onFailActions: List<Action>? = null,
    onSuccessActions: List<Action>? = null,
) = DownloadCallbacks.Properties(
    onFailActions = valueOrNull(onFailActions),
    onSuccessActions = valueOrNull(onSuccessActions),
)

/**
 * @param onFailActions Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
 * @param onSuccessActions Actions in case of successful loading.
 */
@Generated
fun TemplateScope.downloadCallbacksRefs(
    `use named arguments`: Guard = Guard.instance,
    onFailActions: ReferenceProperty<List<Action>>? = null,
    onSuccessActions: ReferenceProperty<List<Action>>? = null,
) = DownloadCallbacks.Properties(
    onFailActions = onFailActions,
    onSuccessActions = onSuccessActions,
)

/**
 * @param onFailActions Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
 * @param onSuccessActions Actions in case of successful loading.
 */
@Generated
fun DownloadCallbacks.override(
    `use named arguments`: Guard = Guard.instance,
    onFailActions: List<Action>? = null,
    onSuccessActions: List<Action>? = null,
): DownloadCallbacks = DownloadCallbacks(
    DownloadCallbacks.Properties(
        onFailActions = valueOrNull(onFailActions) ?: properties.onFailActions,
        onSuccessActions = valueOrNull(onSuccessActions) ?: properties.onSuccessActions,
    )
)

/**
 * @param onFailActions Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
 * @param onSuccessActions Actions in case of successful loading.
 */
@Generated
fun DownloadCallbacks.defer(
    `use named arguments`: Guard = Guard.instance,
    onFailActions: ReferenceProperty<List<Action>>? = null,
    onSuccessActions: ReferenceProperty<List<Action>>? = null,
): DownloadCallbacks = DownloadCallbacks(
    DownloadCallbacks.Properties(
        onFailActions = onFailActions ?: properties.onFailActions,
        onSuccessActions = onSuccessActions ?: properties.onSuccessActions,
    )
)

/**
 * @param onFailActions Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
 * @param onSuccessActions Actions in case of successful loading.
 */
@Generated
fun DownloadCallbacks.modify(
    `use named arguments`: Guard = Guard.instance,
    onFailActions: Property<List<Action>>? = null,
    onSuccessActions: Property<List<Action>>? = null,
): DownloadCallbacks = DownloadCallbacks(
    DownloadCallbacks.Properties(
        onFailActions = onFailActions ?: properties.onFailActions,
        onSuccessActions = onSuccessActions ?: properties.onSuccessActions,
    )
)

@Generated
fun DownloadCallbacks.asList() = listOf(this)
