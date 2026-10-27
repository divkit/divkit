package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.State

@Generated
internal class StateSerializer : EntitySerializer<State>(State::class.java, "state") {
    override fun writeFields(value: State, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ACCESSIBILITY, properties.accessibility, provider)
        gen.writeProperty(ACTION, properties.action, provider)
        gen.writeProperty(ACTION_ANIMATION, properties.actionAnimation, provider)
        gen.writeProperty(ACTIONS, properties.actions, provider)
        gen.writeProperty(ALIGNMENT_HORIZONTAL, properties.alignmentHorizontal, provider)
        gen.writeProperty(ALIGNMENT_VERTICAL, properties.alignmentVertical, provider)
        gen.writeProperty(ALPHA, properties.alpha, provider)
        gen.writeProperty(ANIMATORS, properties.animators, provider)
        gen.writeProperty(BACKGROUND, properties.background, provider)
        gen.writeProperty(BORDER, properties.border, provider)
        gen.writeProperty(CAPTURE_FOCUS_ON_ACTION, properties.captureFocusOnAction, provider)
        gen.writeProperty(CLIP_TO_BOUNDS, properties.clipToBounds, provider)
        gen.writeProperty(COLUMN_SPAN, properties.columnSpan, provider)
        gen.writeProperty(DEFAULT_STATE_ID, properties.defaultStateId, provider)
        gen.writeProperty(DISAPPEAR_ACTIONS, properties.disappearActions, provider)
        gen.writeProperty(DIV_ID, properties.divId, provider)
        gen.writeProperty(DOUBLETAP_ACTIONS, properties.doubletapActions, provider)
        gen.writeProperty(EXTENSIONS, properties.extensions, provider)
        gen.writeProperty(FOCUS, properties.focus, provider)
        gen.writeProperty(FUNCTIONS, properties.functions, provider)
        gen.writeProperty(HEIGHT, properties.height, provider)
        gen.writeProperty(HOVER_END_ACTIONS, properties.hoverEndActions, provider)
        gen.writeProperty(HOVER_START_ACTIONS, properties.hoverStartActions, provider)
        gen.writeProperty(ID, properties.id, provider)
        gen.writeProperty(LAYOUT_PROVIDER, properties.layoutProvider, provider)
        gen.writeProperty(LONGTAP_ACTIONS, properties.longtapActions, provider)
        gen.writeProperty(MARGINS, properties.margins, provider)
        gen.writeProperty(PADDINGS, properties.paddings, provider)
        gen.writeProperty(PRESS_END_ACTIONS, properties.pressEndActions, provider)
        gen.writeProperty(PRESS_START_ACTIONS, properties.pressStartActions, provider)
        gen.writeProperty(REUSE_ID, properties.reuseId, provider)
        gen.writeProperty(ROW_SPAN, properties.rowSpan, provider)
        gen.writeProperty(SELECTED_ACTIONS, properties.selectedActions, provider)
        gen.writeProperty(STATE_ID_VARIABLE, properties.stateIdVariable, provider)
        gen.writeProperty(STATES, properties.states, provider)
        gen.writeProperty(TOOLTIPS, properties.tooltips, provider)
        gen.writeProperty(TRANSFORM, properties.transform, provider)
        gen.writeProperty(TRANSFORMATIONS, properties.transformations, provider)
        gen.writeProperty(TRANSITION_ANIMATION_SELECTOR, properties.transitionAnimationSelector, provider)
        gen.writeProperty(TRANSITION_CHANGE, properties.transitionChange, provider)
        gen.writeProperty(TRANSITION_IN, properties.transitionIn, provider)
        gen.writeProperty(TRANSITION_OUT, properties.transitionOut, provider)
        gen.writeProperty(TRANSITION_TRIGGERS, properties.transitionTriggers, provider)
        gen.writeProperty(VARIABLE_TRIGGERS, properties.variableTriggers, provider)
        gen.writeProperty(VARIABLES, properties.variables, provider)
        gen.writeProperty(VISIBILITY, properties.visibility, provider)
        gen.writeProperty(VISIBILITY_ACTION, properties.visibilityAction, provider)
        gen.writeProperty(VISIBILITY_ACTIONS, properties.visibilityActions, provider)
        gen.writeProperty(WIDTH, properties.width, provider)
    }

    private companion object {
        val ACCESSIBILITY = PropertyName("accessibility")
        val ACTION = PropertyName("action")
        val ACTION_ANIMATION = PropertyName("action_animation")
        val ACTIONS = PropertyName("actions")
        val ALIGNMENT_HORIZONTAL = PropertyName("alignment_horizontal")
        val ALIGNMENT_VERTICAL = PropertyName("alignment_vertical")
        val ALPHA = PropertyName("alpha")
        val ANIMATORS = PropertyName("animators")
        val BACKGROUND = PropertyName("background")
        val BORDER = PropertyName("border")
        val CAPTURE_FOCUS_ON_ACTION = PropertyName("capture_focus_on_action")
        val CLIP_TO_BOUNDS = PropertyName("clip_to_bounds")
        val COLUMN_SPAN = PropertyName("column_span")
        val DEFAULT_STATE_ID = PropertyName("default_state_id")
        val DISAPPEAR_ACTIONS = PropertyName("disappear_actions")
        val DIV_ID = PropertyName("div_id")
        val DOUBLETAP_ACTIONS = PropertyName("doubletap_actions")
        val EXTENSIONS = PropertyName("extensions")
        val FOCUS = PropertyName("focus")
        val FUNCTIONS = PropertyName("functions")
        val HEIGHT = PropertyName("height")
        val HOVER_END_ACTIONS = PropertyName("hover_end_actions")
        val HOVER_START_ACTIONS = PropertyName("hover_start_actions")
        val ID = PropertyName("id")
        val LAYOUT_PROVIDER = PropertyName("layout_provider")
        val LONGTAP_ACTIONS = PropertyName("longtap_actions")
        val MARGINS = PropertyName("margins")
        val PADDINGS = PropertyName("paddings")
        val PRESS_END_ACTIONS = PropertyName("press_end_actions")
        val PRESS_START_ACTIONS = PropertyName("press_start_actions")
        val REUSE_ID = PropertyName("reuse_id")
        val ROW_SPAN = PropertyName("row_span")
        val SELECTED_ACTIONS = PropertyName("selected_actions")
        val STATE_ID_VARIABLE = PropertyName("state_id_variable")
        val STATES = PropertyName("states")
        val TOOLTIPS = PropertyName("tooltips")
        val TRANSFORM = PropertyName("transform")
        val TRANSFORMATIONS = PropertyName("transformations")
        val TRANSITION_ANIMATION_SELECTOR = PropertyName("transition_animation_selector")
        val TRANSITION_CHANGE = PropertyName("transition_change")
        val TRANSITION_IN = PropertyName("transition_in")
        val TRANSITION_OUT = PropertyName("transition_out")
        val TRANSITION_TRIGGERS = PropertyName("transition_triggers")
        val VARIABLE_TRIGGERS = PropertyName("variable_triggers")
        val VARIABLES = PropertyName("variables")
        val VISIBILITY = PropertyName("visibility")
        val VISIBILITY_ACTION = PropertyName("visibility_action")
        val VISIBILITY_ACTIONS = PropertyName("visibility_actions")
        val WIDTH = PropertyName("width")
    }
}

@Generated
internal class StateItemSerializer : EntitySerializer<State.Item>(State.Item::class.java) {
    override fun writeFields(value: State.Item, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ANIMATION_IN, properties.animationIn, provider)
        gen.writeProperty(ANIMATION_OUT, properties.animationOut, provider)
        gen.writeProperty(DIV, properties.div, provider)
        gen.writeProperty(STATE_ID, properties.stateId, provider)
        gen.writeProperty(SWIPE_OUT_ACTIONS, properties.swipeOutActions, provider)
    }

    private companion object {
        val ANIMATION_IN = PropertyName("animation_in")
        val ANIMATION_OUT = PropertyName("animation_out")
        val DIV = PropertyName("div")
        val STATE_ID = PropertyName("state_id")
        val SWIPE_OUT_ACTIONS = PropertyName("swipe_out_actions")
    }
}
