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
 * It defines an action when clicking on an element.
 * 
 * Can be created using the method [action].
 * 
 * Required parameters: `log_id`.
 */
@Generated
class Action internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Action = Action(
        Properties(
            downloadCallbacks = additive.downloadCallbacks ?: properties.downloadCallbacks,
            isEnabled = additive.isEnabled ?: properties.isEnabled,
            logId = additive.logId ?: properties.logId,
            logUrl = additive.logUrl ?: properties.logUrl,
            menuItems = additive.menuItems ?: properties.menuItems,
            payload = additive.payload ?: properties.payload,
            referer = additive.referer ?: properties.referer,
            target = additive.target ?: properties.target,
            typed = additive.typed ?: properties.typed,
            url = additive.url ?: properties.url,
        )
    )

    class Properties internal constructor(
        /**
         * Callbacks that are called after [data loading](../../interaction.dita#loading-data).
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
         * URL for logging.
         */
        val logUrl: Property<Url>?,
        /**
         * Context menu.
         */
        val menuItems: Property<List<MenuItem>>?,
        /**
         * Additional parameters, passed to the host application.
         */
        val payload: Property<Map<String, Any>>?,
        /**
         * Referer URL for logging.
         */
        val referer: Property<Url>?,
        /**
         * The tab in which the URL must be opened.
         */
        val target: Property<Target>?,
        val typed: Property<ActionTyped>?,
        /**
         * URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction.dita).
         */
        val url: Property<Url>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("download_callbacks", downloadCallbacks)
            result.tryPutProperty("is_enabled", isEnabled)
            result.tryPutProperty("log_id", logId)
            result.tryPutProperty("log_url", logUrl)
            result.tryPutProperty("menu_items", menuItems)
            result.tryPutProperty("payload", payload)
            result.tryPutProperty("referer", referer)
            result.tryPutProperty("target", target)
            result.tryPutProperty("typed", typed)
            result.tryPutProperty("url", url)
            return result
        }
    }

    /**
     * The tab in which the URL must be opened.
     * 
     * Possible values: [self], [blank].
     */
    @Generated
    sealed interface Target

    /**
     * Can be created using the method [actionMenuItem].
     * 
     * Required parameters: `text`.
     */
    @Generated
    class MenuItem internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): MenuItem = MenuItem(
            Properties(
                action = additive.action ?: properties.action,
                actions = additive.actions ?: properties.actions,
                text = additive.text ?: properties.text,
            )
        )

        class Properties internal constructor(
            /**
             * One action when clicking on a menu item. Not used if the `actions` parameter is set.
             */
            val action: Property<Action>?,
            /**
             * Multiple actions when clicking on a menu item.
             */
            val actions: Property<List<Action>>?,
            /**
             * Menu item title.
             */
            val text: Property<String>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("action", action)
                result.tryPutProperty("actions", actions)
                result.tryPutProperty("text", text)
                return result
            }
        }
    }

}

/**
 * @param downloadCallbacks Callbacks that are called after [data loading](../../interaction.dita#loading-data).
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logUrl URL for logging.
 * @param menuItems Context menu.
 * @param payload Additional parameters, passed to the host application.
 * @param referer Referer URL for logging.
 * @param target The tab in which the URL must be opened.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction.dita).
 */
@Generated
fun DivScope.action(
    `use named arguments`: Guard = Guard.instance,
    downloadCallbacks: DownloadCallbacks? = null,
    isEnabled: Boolean? = null,
    logId: String? = null,
    logUrl: Url? = null,
    menuItems: List<Action.MenuItem>? = null,
    payload: Map<String, Any>? = null,
    referer: Url? = null,
    target: Action.Target? = null,
    typed: ActionTyped? = null,
    url: Url? = null,
): Action = Action(
    Action.Properties(
        downloadCallbacks = valueOrNull(downloadCallbacks),
        isEnabled = valueOrNull(isEnabled),
        logId = valueOrNull(logId),
        logUrl = valueOrNull(logUrl),
        menuItems = valueOrNull(menuItems),
        payload = valueOrNull(payload),
        referer = valueOrNull(referer),
        target = valueOrNull(target),
        typed = valueOrNull(typed),
        url = valueOrNull(url),
    )
)

/**
 * @param downloadCallbacks Callbacks that are called after [data loading](../../interaction.dita#loading-data).
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logUrl URL for logging.
 * @param menuItems Context menu.
 * @param payload Additional parameters, passed to the host application.
 * @param referer Referer URL for logging.
 * @param target The tab in which the URL must be opened.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction.dita).
 */
@Generated
fun DivScope.actionProps(
    `use named arguments`: Guard = Guard.instance,
    downloadCallbacks: DownloadCallbacks? = null,
    isEnabled: Boolean? = null,
    logId: String? = null,
    logUrl: Url? = null,
    menuItems: List<Action.MenuItem>? = null,
    payload: Map<String, Any>? = null,
    referer: Url? = null,
    target: Action.Target? = null,
    typed: ActionTyped? = null,
    url: Url? = null,
) = Action.Properties(
    downloadCallbacks = valueOrNull(downloadCallbacks),
    isEnabled = valueOrNull(isEnabled),
    logId = valueOrNull(logId),
    logUrl = valueOrNull(logUrl),
    menuItems = valueOrNull(menuItems),
    payload = valueOrNull(payload),
    referer = valueOrNull(referer),
    target = valueOrNull(target),
    typed = valueOrNull(typed),
    url = valueOrNull(url),
)

/**
 * @param downloadCallbacks Callbacks that are called after [data loading](../../interaction.dita#loading-data).
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logUrl URL for logging.
 * @param menuItems Context menu.
 * @param payload Additional parameters, passed to the host application.
 * @param referer Referer URL for logging.
 * @param target The tab in which the URL must be opened.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction.dita).
 */
@Generated
fun TemplateScope.actionRefs(
    `use named arguments`: Guard = Guard.instance,
    downloadCallbacks: ReferenceProperty<DownloadCallbacks>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
    logId: ReferenceProperty<String>? = null,
    logUrl: ReferenceProperty<Url>? = null,
    menuItems: ReferenceProperty<List<Action.MenuItem>>? = null,
    payload: ReferenceProperty<Map<String, Any>>? = null,
    referer: ReferenceProperty<Url>? = null,
    target: ReferenceProperty<Action.Target>? = null,
    typed: ReferenceProperty<ActionTyped>? = null,
    url: ReferenceProperty<Url>? = null,
) = Action.Properties(
    downloadCallbacks = downloadCallbacks,
    isEnabled = isEnabled,
    logId = logId,
    logUrl = logUrl,
    menuItems = menuItems,
    payload = payload,
    referer = referer,
    target = target,
    typed = typed,
    url = url,
)

/**
 * @param downloadCallbacks Callbacks that are called after [data loading](../../interaction.dita#loading-data).
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logUrl URL for logging.
 * @param menuItems Context menu.
 * @param payload Additional parameters, passed to the host application.
 * @param referer Referer URL for logging.
 * @param target The tab in which the URL must be opened.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction.dita).
 */
@Generated
fun Action.override(
    `use named arguments`: Guard = Guard.instance,
    downloadCallbacks: DownloadCallbacks? = null,
    isEnabled: Boolean? = null,
    logId: String? = null,
    logUrl: Url? = null,
    menuItems: List<Action.MenuItem>? = null,
    payload: Map<String, Any>? = null,
    referer: Url? = null,
    target: Action.Target? = null,
    typed: ActionTyped? = null,
    url: Url? = null,
): Action = Action(
    Action.Properties(
        downloadCallbacks = valueOrNull(downloadCallbacks) ?: properties.downloadCallbacks,
        isEnabled = valueOrNull(isEnabled) ?: properties.isEnabled,
        logId = valueOrNull(logId) ?: properties.logId,
        logUrl = valueOrNull(logUrl) ?: properties.logUrl,
        menuItems = valueOrNull(menuItems) ?: properties.menuItems,
        payload = valueOrNull(payload) ?: properties.payload,
        referer = valueOrNull(referer) ?: properties.referer,
        target = valueOrNull(target) ?: properties.target,
        typed = valueOrNull(typed) ?: properties.typed,
        url = valueOrNull(url) ?: properties.url,
    )
)

/**
 * @param downloadCallbacks Callbacks that are called after [data loading](../../interaction.dita#loading-data).
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logId Logging ID.
 * @param logUrl URL for logging.
 * @param menuItems Context menu.
 * @param payload Additional parameters, passed to the host application.
 * @param referer Referer URL for logging.
 * @param target The tab in which the URL must be opened.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction.dita).
 */
@Generated
fun Action.defer(
    `use named arguments`: Guard = Guard.instance,
    downloadCallbacks: ReferenceProperty<DownloadCallbacks>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
    logId: ReferenceProperty<String>? = null,
    logUrl: ReferenceProperty<Url>? = null,
    menuItems: ReferenceProperty<List<Action.MenuItem>>? = null,
    payload: ReferenceProperty<Map<String, Any>>? = null,
    referer: ReferenceProperty<Url>? = null,
    target: ReferenceProperty<Action.Target>? = null,
    typed: ReferenceProperty<ActionTyped>? = null,
    url: ReferenceProperty<Url>? = null,
): Action = Action(
    Action.Properties(
        downloadCallbacks = downloadCallbacks ?: properties.downloadCallbacks,
        isEnabled = isEnabled ?: properties.isEnabled,
        logId = logId ?: properties.logId,
        logUrl = logUrl ?: properties.logUrl,
        menuItems = menuItems ?: properties.menuItems,
        payload = payload ?: properties.payload,
        referer = referer ?: properties.referer,
        target = target ?: properties.target,
        typed = typed ?: properties.typed,
        url = url ?: properties.url,
    )
)

/**
 * @param isEnabled The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
 * @param logUrl URL for logging.
 * @param referer Referer URL for logging.
 * @param target The tab in which the URL must be opened.
 * @param url URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction.dita).
 */
@Generated
fun Action.evaluate(
    `use named arguments`: Guard = Guard.instance,
    isEnabled: ExpressionProperty<Boolean>? = null,
    logUrl: ExpressionProperty<Url>? = null,
    referer: ExpressionProperty<Url>? = null,
    target: ExpressionProperty<Action.Target>? = null,
    url: ExpressionProperty<Url>? = null,
): Action = Action(
    Action.Properties(
        downloadCallbacks = properties.downloadCallbacks,
        isEnabled = isEnabled ?: properties.isEnabled,
        logId = properties.logId,
        logUrl = logUrl ?: properties.logUrl,
        menuItems = properties.menuItems,
        payload = properties.payload,
        referer = referer ?: properties.referer,
        target = target ?: properties.target,
        typed = properties.typed,
        url = url ?: properties.url,
    )
)

@Generated
fun Action.asList() = listOf(this)

/**
 * @param action One action when clicking on a menu item. Not used if the `actions` parameter is set.
 * @param actions Multiple actions when clicking on a menu item.
 * @param text Menu item title.
 */
@Generated
fun DivScope.actionMenuItem(
    `use named arguments`: Guard = Guard.instance,
    action: Action? = null,
    actions: List<Action>? = null,
    text: String? = null,
): Action.MenuItem = Action.MenuItem(
    Action.MenuItem.Properties(
        action = valueOrNull(action),
        actions = valueOrNull(actions),
        text = valueOrNull(text),
    )
)

/**
 * @param action One action when clicking on a menu item. Not used if the `actions` parameter is set.
 * @param actions Multiple actions when clicking on a menu item.
 * @param text Menu item title.
 */
@Generated
fun DivScope.actionMenuItemProps(
    `use named arguments`: Guard = Guard.instance,
    action: Action? = null,
    actions: List<Action>? = null,
    text: String? = null,
) = Action.MenuItem.Properties(
    action = valueOrNull(action),
    actions = valueOrNull(actions),
    text = valueOrNull(text),
)

/**
 * @param action One action when clicking on a menu item. Not used if the `actions` parameter is set.
 * @param actions Multiple actions when clicking on a menu item.
 * @param text Menu item title.
 */
@Generated
fun TemplateScope.actionMenuItemRefs(
    `use named arguments`: Guard = Guard.instance,
    action: ReferenceProperty<Action>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    text: ReferenceProperty<String>? = null,
) = Action.MenuItem.Properties(
    action = action,
    actions = actions,
    text = text,
)

/**
 * @param action One action when clicking on a menu item. Not used if the `actions` parameter is set.
 * @param actions Multiple actions when clicking on a menu item.
 * @param text Menu item title.
 */
@Generated
fun Action.MenuItem.override(
    `use named arguments`: Guard = Guard.instance,
    action: Action? = null,
    actions: List<Action>? = null,
    text: String? = null,
): Action.MenuItem = Action.MenuItem(
    Action.MenuItem.Properties(
        action = valueOrNull(action) ?: properties.action,
        actions = valueOrNull(actions) ?: properties.actions,
        text = valueOrNull(text) ?: properties.text,
    )
)

/**
 * @param action One action when clicking on a menu item. Not used if the `actions` parameter is set.
 * @param actions Multiple actions when clicking on a menu item.
 * @param text Menu item title.
 */
@Generated
fun Action.MenuItem.defer(
    `use named arguments`: Guard = Guard.instance,
    action: ReferenceProperty<Action>? = null,
    actions: ReferenceProperty<List<Action>>? = null,
    text: ReferenceProperty<String>? = null,
): Action.MenuItem = Action.MenuItem(
    Action.MenuItem.Properties(
        action = action ?: properties.action,
        actions = actions ?: properties.actions,
        text = text ?: properties.text,
    )
)

/**
 * @param text Menu item title.
 */
@Generated
fun Action.MenuItem.evaluate(
    `use named arguments`: Guard = Guard.instance,
    text: ExpressionProperty<String>? = null,
): Action.MenuItem = Action.MenuItem(
    Action.MenuItem.Properties(
        action = properties.action,
        actions = properties.actions,
        text = text ?: properties.text,
    )
)

@Generated
fun Action.MenuItem.asList() = listOf(this)

@Generated
fun Action.Target.asList() = listOf(this)
