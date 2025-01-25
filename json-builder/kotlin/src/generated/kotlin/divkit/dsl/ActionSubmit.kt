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
 * Sends variables from the container by link. Data sending configuration can be defined by the host app. By default, variables are sent as JSON in the request body using the POST method.
 * 
 * Can be created using the method [actionSubmit].
 * 
 * Required parameters: `type, request, container_id`.
 */
@Generated
data class ActionSubmit internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "submit")
    )

    operator fun plus(additive: Properties): ActionSubmit = ActionSubmit(
        Properties(
            containerId = additive.containerId ?: properties.containerId,
            onFailActions = additive.onFailActions ?: properties.onFailActions,
            onSuccessActions = additive.onSuccessActions ?: properties.onSuccessActions,
            request = additive.request ?: properties.request,
        )
    )

    data class Properties internal constructor(
        /**
         * ID of the container with the variables to be sent.
         */
        val containerId: Property<String>?,
        /**
         * Actions when sending data is unsuccessful.
         */
        val onFailActions: Property<List<Action>>?,
        /**
         * Actions when sending data is successful.
         */
        val onSuccessActions: Property<List<Action>>?,
        /**
         * HTTP request parameters for configuring the sending of data.
         */
        val request: Property<Request>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("container_id", containerId)
            result.tryPutProperty("on_fail_actions", onFailActions)
            result.tryPutProperty("on_success_actions", onSuccessActions)
            result.tryPutProperty("request", request)
            return result
        }
    }

    /**
     * HTTP request parameters for configuring the sending of data.
     * 
     * Can be created using the method [actionSubmitRequest].
     * 
     * Required parameters: `url`.
     */
    @Generated
    data class Request internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): Request = Request(
            Properties(
                headers = additive.headers ?: properties.headers,
                method = additive.method ?: properties.method,
                url = additive.url ?: properties.url,
            )
        )

        data class Properties internal constructor(
            /**
             * HTTP request headers.
             */
            val headers: Property<List<Header>>?,
            /**
             * HTTP request method.
             * Default value: `post`.
             */
            val method: Property<Method>?,
            /**
             * Link for sending data from the container.
             */
            val url: Property<Url>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("headers", headers)
                result.tryPutProperty("method", method)
                result.tryPutProperty("url", url)
                return result
            }
        }

        /**
         * HTTP request method.
         * 
         * Possible values: [get], [post], [put], [patch], [delete], [head], [options].
         */
        @Generated
        sealed interface Method

        /**
         * Can be created using the method [actionSubmitRequestHeader].
         * 
         * Required parameters: `value, name`.
         */
        @Generated
        data class Header internal constructor(
            @JsonIgnore
            val properties: Properties,
        ) {
            @JsonAnyGetter
            internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

            operator fun plus(additive: Properties): Header = Header(
                Properties(
                    name = additive.name ?: properties.name,
                    value = additive.value ?: properties.value,
                )
            )

            data class Properties internal constructor(
                val name: Property<String>?,
                val value: Property<String>?,
            ) {
                internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                    val result = mutableMapOf<String, Any>()
                    result.putAll(properties)
                    result.tryPutProperty("name", name)
                    result.tryPutProperty("value", value)
                    return result
                }
            }
        }

    }

}

/**
 * @param containerId ID of the container with the variables to be sent.
 * @param onFailActions Actions when sending data is unsuccessful.
 * @param onSuccessActions Actions when sending data is successful.
 * @param request HTTP request parameters for configuring the sending of data.
 */
@Generated
fun DivScope.actionSubmit(
    `use named arguments`: Guard = Guard.instance,
    containerId: String? = null,
    onFailActions: List<Action>? = null,
    onSuccessActions: List<Action>? = null,
    request: ActionSubmit.Request? = null,
): ActionSubmit = ActionSubmit(
    ActionSubmit.Properties(
        containerId = valueOrNull(containerId),
        onFailActions = valueOrNull(onFailActions),
        onSuccessActions = valueOrNull(onSuccessActions),
        request = valueOrNull(request),
    )
)

/**
 * @param containerId ID of the container with the variables to be sent.
 * @param onFailActions Actions when sending data is unsuccessful.
 * @param onSuccessActions Actions when sending data is successful.
 * @param request HTTP request parameters for configuring the sending of data.
 */
@Generated
fun DivScope.actionSubmitProps(
    `use named arguments`: Guard = Guard.instance,
    containerId: String? = null,
    onFailActions: List<Action>? = null,
    onSuccessActions: List<Action>? = null,
    request: ActionSubmit.Request? = null,
) = ActionSubmit.Properties(
    containerId = valueOrNull(containerId),
    onFailActions = valueOrNull(onFailActions),
    onSuccessActions = valueOrNull(onSuccessActions),
    request = valueOrNull(request),
)

/**
 * @param containerId ID of the container with the variables to be sent.
 * @param onFailActions Actions when sending data is unsuccessful.
 * @param onSuccessActions Actions when sending data is successful.
 * @param request HTTP request parameters for configuring the sending of data.
 */
@Generated
fun TemplateScope.actionSubmitRefs(
    `use named arguments`: Guard = Guard.instance,
    containerId: ReferenceProperty<String>? = null,
    onFailActions: ReferenceProperty<List<Action>>? = null,
    onSuccessActions: ReferenceProperty<List<Action>>? = null,
    request: ReferenceProperty<ActionSubmit.Request>? = null,
) = ActionSubmit.Properties(
    containerId = containerId,
    onFailActions = onFailActions,
    onSuccessActions = onSuccessActions,
    request = request,
)

/**
 * @param containerId ID of the container with the variables to be sent.
 * @param onFailActions Actions when sending data is unsuccessful.
 * @param onSuccessActions Actions when sending data is successful.
 * @param request HTTP request parameters for configuring the sending of data.
 */
@Generated
fun ActionSubmit.override(
    `use named arguments`: Guard = Guard.instance,
    containerId: String? = null,
    onFailActions: List<Action>? = null,
    onSuccessActions: List<Action>? = null,
    request: ActionSubmit.Request? = null,
): ActionSubmit = ActionSubmit(
    ActionSubmit.Properties(
        containerId = valueOrNull(containerId) ?: properties.containerId,
        onFailActions = valueOrNull(onFailActions) ?: properties.onFailActions,
        onSuccessActions = valueOrNull(onSuccessActions) ?: properties.onSuccessActions,
        request = valueOrNull(request) ?: properties.request,
    )
)

/**
 * @param containerId ID of the container with the variables to be sent.
 * @param onFailActions Actions when sending data is unsuccessful.
 * @param onSuccessActions Actions when sending data is successful.
 * @param request HTTP request parameters for configuring the sending of data.
 */
@Generated
fun ActionSubmit.defer(
    `use named arguments`: Guard = Guard.instance,
    containerId: ReferenceProperty<String>? = null,
    onFailActions: ReferenceProperty<List<Action>>? = null,
    onSuccessActions: ReferenceProperty<List<Action>>? = null,
    request: ReferenceProperty<ActionSubmit.Request>? = null,
): ActionSubmit = ActionSubmit(
    ActionSubmit.Properties(
        containerId = containerId ?: properties.containerId,
        onFailActions = onFailActions ?: properties.onFailActions,
        onSuccessActions = onSuccessActions ?: properties.onSuccessActions,
        request = request ?: properties.request,
    )
)

/**
 * @param containerId ID of the container with the variables to be sent.
 */
@Generated
fun ActionSubmit.evaluate(
    `use named arguments`: Guard = Guard.instance,
    containerId: ExpressionProperty<String>? = null,
): ActionSubmit = ActionSubmit(
    ActionSubmit.Properties(
        containerId = containerId ?: properties.containerId,
        onFailActions = properties.onFailActions,
        onSuccessActions = properties.onSuccessActions,
        request = properties.request,
    )
)

@Generated
fun ActionSubmit.asList() = listOf(this)

/**
 * @param headers HTTP request headers.
 * @param method HTTP request method.
 * @param url Link for sending data from the container.
 */
@Generated
fun DivScope.actionSubmitRequest(
    `use named arguments`: Guard = Guard.instance,
    headers: List<ActionSubmit.Request.Header>? = null,
    method: ActionSubmit.Request.Method? = null,
    url: Url? = null,
): ActionSubmit.Request = ActionSubmit.Request(
    ActionSubmit.Request.Properties(
        headers = valueOrNull(headers),
        method = valueOrNull(method),
        url = valueOrNull(url),
    )
)

/**
 * @param headers HTTP request headers.
 * @param method HTTP request method.
 * @param url Link for sending data from the container.
 */
@Generated
fun DivScope.actionSubmitRequestProps(
    `use named arguments`: Guard = Guard.instance,
    headers: List<ActionSubmit.Request.Header>? = null,
    method: ActionSubmit.Request.Method? = null,
    url: Url? = null,
) = ActionSubmit.Request.Properties(
    headers = valueOrNull(headers),
    method = valueOrNull(method),
    url = valueOrNull(url),
)

/**
 * @param headers HTTP request headers.
 * @param method HTTP request method.
 * @param url Link for sending data from the container.
 */
@Generated
fun TemplateScope.actionSubmitRequestRefs(
    `use named arguments`: Guard = Guard.instance,
    headers: ReferenceProperty<List<ActionSubmit.Request.Header>>? = null,
    method: ReferenceProperty<ActionSubmit.Request.Method>? = null,
    url: ReferenceProperty<Url>? = null,
) = ActionSubmit.Request.Properties(
    headers = headers,
    method = method,
    url = url,
)

/**
 * @param headers HTTP request headers.
 * @param method HTTP request method.
 * @param url Link for sending data from the container.
 */
@Generated
fun ActionSubmit.Request.override(
    `use named arguments`: Guard = Guard.instance,
    headers: List<ActionSubmit.Request.Header>? = null,
    method: ActionSubmit.Request.Method? = null,
    url: Url? = null,
): ActionSubmit.Request = ActionSubmit.Request(
    ActionSubmit.Request.Properties(
        headers = valueOrNull(headers) ?: properties.headers,
        method = valueOrNull(method) ?: properties.method,
        url = valueOrNull(url) ?: properties.url,
    )
)

/**
 * @param headers HTTP request headers.
 * @param method HTTP request method.
 * @param url Link for sending data from the container.
 */
@Generated
fun ActionSubmit.Request.defer(
    `use named arguments`: Guard = Guard.instance,
    headers: ReferenceProperty<List<ActionSubmit.Request.Header>>? = null,
    method: ReferenceProperty<ActionSubmit.Request.Method>? = null,
    url: ReferenceProperty<Url>? = null,
): ActionSubmit.Request = ActionSubmit.Request(
    ActionSubmit.Request.Properties(
        headers = headers ?: properties.headers,
        method = method ?: properties.method,
        url = url ?: properties.url,
    )
)

/**
 * @param method HTTP request method.
 * @param url Link for sending data from the container.
 */
@Generated
fun ActionSubmit.Request.evaluate(
    `use named arguments`: Guard = Guard.instance,
    method: ExpressionProperty<ActionSubmit.Request.Method>? = null,
    url: ExpressionProperty<Url>? = null,
): ActionSubmit.Request = ActionSubmit.Request(
    ActionSubmit.Request.Properties(
        headers = properties.headers,
        method = method ?: properties.method,
        url = url ?: properties.url,
    )
)

@Generated
fun ActionSubmit.Request.asList() = listOf(this)
