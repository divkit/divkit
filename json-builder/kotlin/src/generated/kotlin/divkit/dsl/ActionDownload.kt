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
 * Loads additional data in `div-patch` format and updates the current element.
 * 
 * Can be created using the method [actionDownload].
 * 
 * Required parameters: `url, type`.
 */
@Generated
data class ActionDownload internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "download")
    )

    operator fun plus(additive: Properties): ActionDownload = ActionDownload(
        Properties(
            onFailActions = additive.onFailActions ?: properties.onFailActions,
            onSuccessActions = additive.onSuccessActions ?: properties.onSuccessActions,
            url = additive.url ?: properties.url,
        )
    )

    data class Properties internal constructor(
        /**
         * Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
         */
        val onFailActions: Property<List<Action>>?,
        /**
         * Actions in case of successful loading.
         */
        val onSuccessActions: Property<List<Action>>?,
        /**
         * Link for receiving changes.
         */
        val url: Property<Url>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("on_fail_actions", onFailActions)
            result.tryPutProperty("on_success_actions", onSuccessActions)
            result.tryPutProperty("url", url)
            return result
        }
    }
}

/**
 * @param onFailActions Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
 * @param onSuccessActions Actions in case of successful loading.
 * @param url Link for receiving changes.
 */
@Generated
fun DivScope.actionDownload(
    `use named arguments`: Guard = Guard.instance,
    onFailActions: List<Action>? = null,
    onSuccessActions: List<Action>? = null,
    url: Url? = null,
): ActionDownload = ActionDownload(
    ActionDownload.Properties(
        onFailActions = valueOrNull(onFailActions),
        onSuccessActions = valueOrNull(onSuccessActions),
        url = valueOrNull(url),
    )
)

/**
 * @param onFailActions Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
 * @param onSuccessActions Actions in case of successful loading.
 * @param url Link for receiving changes.
 */
@Generated
fun DivScope.actionDownloadProps(
    `use named arguments`: Guard = Guard.instance,
    onFailActions: List<Action>? = null,
    onSuccessActions: List<Action>? = null,
    url: Url? = null,
) = ActionDownload.Properties(
    onFailActions = valueOrNull(onFailActions),
    onSuccessActions = valueOrNull(onSuccessActions),
    url = valueOrNull(url),
)

/**
 * @param onFailActions Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
 * @param onSuccessActions Actions in case of successful loading.
 * @param url Link for receiving changes.
 */
@Generated
fun TemplateScope.actionDownloadRefs(
    `use named arguments`: Guard = Guard.instance,
    onFailActions: ReferenceProperty<List<Action>>? = null,
    onSuccessActions: ReferenceProperty<List<Action>>? = null,
    url: ReferenceProperty<Url>? = null,
) = ActionDownload.Properties(
    onFailActions = onFailActions,
    onSuccessActions = onSuccessActions,
    url = url,
)

/**
 * @param onFailActions Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
 * @param onSuccessActions Actions in case of successful loading.
 * @param url Link for receiving changes.
 */
@Generated
fun ActionDownload.override(
    `use named arguments`: Guard = Guard.instance,
    onFailActions: List<Action>? = null,
    onSuccessActions: List<Action>? = null,
    url: Url? = null,
): ActionDownload = ActionDownload(
    ActionDownload.Properties(
        onFailActions = valueOrNull(onFailActions) ?: properties.onFailActions,
        onSuccessActions = valueOrNull(onSuccessActions) ?: properties.onSuccessActions,
        url = valueOrNull(url) ?: properties.url,
    )
)

/**
 * @param onFailActions Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
 * @param onSuccessActions Actions in case of successful loading.
 * @param url Link for receiving changes.
 */
@Generated
fun ActionDownload.defer(
    `use named arguments`: Guard = Guard.instance,
    onFailActions: ReferenceProperty<List<Action>>? = null,
    onSuccessActions: ReferenceProperty<List<Action>>? = null,
    url: ReferenceProperty<Url>? = null,
): ActionDownload = ActionDownload(
    ActionDownload.Properties(
        onFailActions = onFailActions ?: properties.onFailActions,
        onSuccessActions = onSuccessActions ?: properties.onSuccessActions,
        url = url ?: properties.url,
    )
)

/**
 * @param onFailActions Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
 * @param onSuccessActions Actions in case of successful loading.
 * @param url Link for receiving changes.
 */
@Generated
fun ActionDownload.modify(
    `use named arguments`: Guard = Guard.instance,
    onFailActions: Property<List<Action>>? = null,
    onSuccessActions: Property<List<Action>>? = null,
    url: Property<Url>? = null,
): ActionDownload = ActionDownload(
    ActionDownload.Properties(
        onFailActions = onFailActions ?: properties.onFailActions,
        onSuccessActions = onSuccessActions ?: properties.onSuccessActions,
        url = url ?: properties.url,
    )
)

/**
 * @param url Link for receiving changes.
 */
@Generated
fun ActionDownload.evaluate(
    `use named arguments`: Guard = Guard.instance,
    url: ExpressionProperty<Url>? = null,
): ActionDownload = ActionDownload(
    ActionDownload.Properties(
        onFailActions = properties.onFailActions,
        onSuccessActions = properties.onSuccessActions,
        url = url ?: properties.url,
    )
)

@Generated
fun ActionDownload.asList() = listOf(this)
