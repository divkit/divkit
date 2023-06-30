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
 * Accessibility settings.
 * 
 * Can be created using the method [accessibility].
 */
@Generated
class Accessibility internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Accessibility = Accessibility(
        Properties(
            description = additive.description ?: properties.description,
            hint = additive.hint ?: properties.hint,
            mode = additive.mode ?: properties.mode,
            muteAfterAction = additive.muteAfterAction ?: properties.muteAfterAction,
            stateDescription = additive.stateDescription ?: properties.stateDescription,
            type = additive.type ?: properties.type,
        )
    )

    class Properties internal constructor(
        /**
         * Element description. It is used as the main description for screen reading applications.
         */
        val description: Property<String>?,
        /**
         * A tooltip of what will happen during interaction. If Speak Hints is enabled in the VoiceOver settings on iOS, a tooltip is played after `description`.
         */
        val hint: Property<String>?,
        /**
         * The way the accessibility tree is organized. In the `merge` mode the accessibility service perceives an element together with a subtree as a whole. In the `exclude` mode an element together with a subtree isn't available for accessibility.
         * Default value: `default`.
         */
        val mode: Property<Mode>?,
        /**
         * Mutes the screen reader sound after interacting with the element.
         * Default value: `false`.
         */
        val muteAfterAction: Property<Boolean>?,
        /**
         * Description of the current state of an element. For example, in the description you can specify a selected date for a date selection element and an on/off state for a switch.
         */
        val stateDescription: Property<String>?,
        /**
         * Element role. Used to correctly identify an element by the accessibility service. For example, the `list` element is used to group list elements into one element.
         */
        val type: Property<Type>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("description", description)
            result.tryPutProperty("hint", hint)
            result.tryPutProperty("mode", mode)
            result.tryPutProperty("mute_after_action", muteAfterAction)
            result.tryPutProperty("state_description", stateDescription)
            result.tryPutProperty("type", type)
            return result
        }
    }

    /**
     * The way the accessibility tree is organized. In the `merge` mode the accessibility service perceives an element together with a subtree as a whole. In the `exclude` mode an element together with a subtree isn't available for accessibility.
     * 
     * Possible values: [default, merge, exclude].
     */
    @Generated
    sealed interface Mode

    /**
     * Element role. Used to correctly identify an element by the accessibility service. For example, the `list` element is used to group list elements into one element.
     * 
     * Possible values: [none, button, image, text, edit_text, header, tab_bar, list, select].
     */
    @Generated
    sealed interface Type
}

/**
 * @param description Element description. It is used as the main description for screen reading applications.
 * @param hint A tooltip of what will happen during interaction. If Speak Hints is enabled in the VoiceOver settings on iOS, a tooltip is played after `description`.
 * @param mode The way the accessibility tree is organized. In the `merge` mode the accessibility service perceives an element together with a subtree as a whole. In the `exclude` mode an element together with a subtree isn't available for accessibility.
 * @param muteAfterAction Mutes the screen reader sound after interacting with the element.
 * @param stateDescription Description of the current state of an element. For example, in the description you can specify a selected date for a date selection element and an on/off state for a switch.
 * @param type Element role. Used to correctly identify an element by the accessibility service. For example, the `list` element is used to group list elements into one element.
 */
@Generated
fun DivScope.accessibility(
    `use named arguments`: Guard = Guard.instance,
    description: String? = null,
    hint: String? = null,
    mode: Accessibility.Mode? = null,
    muteAfterAction: Boolean? = null,
    stateDescription: String? = null,
    type: Accessibility.Type? = null,
): Accessibility = Accessibility(
    Accessibility.Properties(
        description = valueOrNull(description),
        hint = valueOrNull(hint),
        mode = valueOrNull(mode),
        muteAfterAction = valueOrNull(muteAfterAction),
        stateDescription = valueOrNull(stateDescription),
        type = valueOrNull(type),
    )
)

/**
 * @param description Element description. It is used as the main description for screen reading applications.
 * @param hint A tooltip of what will happen during interaction. If Speak Hints is enabled in the VoiceOver settings on iOS, a tooltip is played after `description`.
 * @param mode The way the accessibility tree is organized. In the `merge` mode the accessibility service perceives an element together with a subtree as a whole. In the `exclude` mode an element together with a subtree isn't available for accessibility.
 * @param muteAfterAction Mutes the screen reader sound after interacting with the element.
 * @param stateDescription Description of the current state of an element. For example, in the description you can specify a selected date for a date selection element and an on/off state for a switch.
 * @param type Element role. Used to correctly identify an element by the accessibility service. For example, the `list` element is used to group list elements into one element.
 */
@Generated
fun DivScope.accessibilityProps(
    `use named arguments`: Guard = Guard.instance,
    description: String? = null,
    hint: String? = null,
    mode: Accessibility.Mode? = null,
    muteAfterAction: Boolean? = null,
    stateDescription: String? = null,
    type: Accessibility.Type? = null,
) = Accessibility.Properties(
    description = valueOrNull(description),
    hint = valueOrNull(hint),
    mode = valueOrNull(mode),
    muteAfterAction = valueOrNull(muteAfterAction),
    stateDescription = valueOrNull(stateDescription),
    type = valueOrNull(type),
)

/**
 * @param description Element description. It is used as the main description for screen reading applications.
 * @param hint A tooltip of what will happen during interaction. If Speak Hints is enabled in the VoiceOver settings on iOS, a tooltip is played after `description`.
 * @param mode The way the accessibility tree is organized. In the `merge` mode the accessibility service perceives an element together with a subtree as a whole. In the `exclude` mode an element together with a subtree isn't available for accessibility.
 * @param muteAfterAction Mutes the screen reader sound after interacting with the element.
 * @param stateDescription Description of the current state of an element. For example, in the description you can specify a selected date for a date selection element and an on/off state for a switch.
 * @param type Element role. Used to correctly identify an element by the accessibility service. For example, the `list` element is used to group list elements into one element.
 */
@Generated
fun TemplateScope.accessibilityRefs(
    `use named arguments`: Guard = Guard.instance,
    description: ReferenceProperty<String>? = null,
    hint: ReferenceProperty<String>? = null,
    mode: ReferenceProperty<Accessibility.Mode>? = null,
    muteAfterAction: ReferenceProperty<Boolean>? = null,
    stateDescription: ReferenceProperty<String>? = null,
    type: ReferenceProperty<Accessibility.Type>? = null,
) = Accessibility.Properties(
    description = description,
    hint = hint,
    mode = mode,
    muteAfterAction = muteAfterAction,
    stateDescription = stateDescription,
    type = type,
)

/**
 * @param description Element description. It is used as the main description for screen reading applications.
 * @param hint A tooltip of what will happen during interaction. If Speak Hints is enabled in the VoiceOver settings on iOS, a tooltip is played after `description`.
 * @param mode The way the accessibility tree is organized. In the `merge` mode the accessibility service perceives an element together with a subtree as a whole. In the `exclude` mode an element together with a subtree isn't available for accessibility.
 * @param muteAfterAction Mutes the screen reader sound after interacting with the element.
 * @param stateDescription Description of the current state of an element. For example, in the description you can specify a selected date for a date selection element and an on/off state for a switch.
 * @param type Element role. Used to correctly identify an element by the accessibility service. For example, the `list` element is used to group list elements into one element.
 */
@Generated
fun Accessibility.override(
    `use named arguments`: Guard = Guard.instance,
    description: String? = null,
    hint: String? = null,
    mode: Accessibility.Mode? = null,
    muteAfterAction: Boolean? = null,
    stateDescription: String? = null,
    type: Accessibility.Type? = null,
): Accessibility = Accessibility(
    Accessibility.Properties(
        description = valueOrNull(description) ?: properties.description,
        hint = valueOrNull(hint) ?: properties.hint,
        mode = valueOrNull(mode) ?: properties.mode,
        muteAfterAction = valueOrNull(muteAfterAction) ?: properties.muteAfterAction,
        stateDescription = valueOrNull(stateDescription) ?: properties.stateDescription,
        type = valueOrNull(type) ?: properties.type,
    )
)

/**
 * @param description Element description. It is used as the main description for screen reading applications.
 * @param hint A tooltip of what will happen during interaction. If Speak Hints is enabled in the VoiceOver settings on iOS, a tooltip is played after `description`.
 * @param mode The way the accessibility tree is organized. In the `merge` mode the accessibility service perceives an element together with a subtree as a whole. In the `exclude` mode an element together with a subtree isn't available for accessibility.
 * @param muteAfterAction Mutes the screen reader sound after interacting with the element.
 * @param stateDescription Description of the current state of an element. For example, in the description you can specify a selected date for a date selection element and an on/off state for a switch.
 * @param type Element role. Used to correctly identify an element by the accessibility service. For example, the `list` element is used to group list elements into one element.
 */
@Generated
fun Accessibility.defer(
    `use named arguments`: Guard = Guard.instance,
    description: ReferenceProperty<String>? = null,
    hint: ReferenceProperty<String>? = null,
    mode: ReferenceProperty<Accessibility.Mode>? = null,
    muteAfterAction: ReferenceProperty<Boolean>? = null,
    stateDescription: ReferenceProperty<String>? = null,
    type: ReferenceProperty<Accessibility.Type>? = null,
): Accessibility = Accessibility(
    Accessibility.Properties(
        description = description ?: properties.description,
        hint = hint ?: properties.hint,
        mode = mode ?: properties.mode,
        muteAfterAction = muteAfterAction ?: properties.muteAfterAction,
        stateDescription = stateDescription ?: properties.stateDescription,
        type = type ?: properties.type,
    )
)

/**
 * @param description Element description. It is used as the main description for screen reading applications.
 * @param hint A tooltip of what will happen during interaction. If Speak Hints is enabled in the VoiceOver settings on iOS, a tooltip is played after `description`.
 * @param mode The way the accessibility tree is organized. In the `merge` mode the accessibility service perceives an element together with a subtree as a whole. In the `exclude` mode an element together with a subtree isn't available for accessibility.
 * @param muteAfterAction Mutes the screen reader sound after interacting with the element.
 * @param stateDescription Description of the current state of an element. For example, in the description you can specify a selected date for a date selection element and an on/off state for a switch.
 */
@Generated
fun Accessibility.evaluate(
    `use named arguments`: Guard = Guard.instance,
    description: ExpressionProperty<String>? = null,
    hint: ExpressionProperty<String>? = null,
    mode: ExpressionProperty<Accessibility.Mode>? = null,
    muteAfterAction: ExpressionProperty<Boolean>? = null,
    stateDescription: ExpressionProperty<String>? = null,
): Accessibility = Accessibility(
    Accessibility.Properties(
        description = description ?: properties.description,
        hint = hint ?: properties.hint,
        mode = mode ?: properties.mode,
        muteAfterAction = muteAfterAction ?: properties.muteAfterAction,
        stateDescription = stateDescription ?: properties.stateDescription,
        type = properties.type,
    )
)

@Generated
fun Accessibility.asList() = listOf(this)

@Generated
fun Accessibility.Mode.asList() = listOf(this)

@Generated
fun Accessibility.Type.asList() = listOf(this)
