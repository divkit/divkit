# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import (
    div, div_accessibility, div_action, div_alignment_horizontal,
    div_alignment_vertical, div_animation, div_appearance_transition,
    div_background, div_border, div_change_transition, div_disappear_action,
    div_edge_insets, div_extension, div_focus, div_size, div_tooltip,
    div_transform, div_transition_selector, div_transition_trigger,
    div_visibility, div_visibility_action,
)


# It contains sets of states for visual elements and switches between them.
class DivState(BaseDiv):

    def __init__(
        self, *,
        type: str = "state",
        accessibility: typing.Optional[div_accessibility.DivAccessibility] = None,
        alignment_horizontal: typing.Optional[typing.Union[Expr, div_alignment_horizontal.DivAlignmentHorizontal]] = None,
        alignment_vertical: typing.Optional[typing.Union[Expr, div_alignment_vertical.DivAlignmentVertical]] = None,
        alpha: typing.Optional[typing.Union[Expr, float]] = None,
        background: typing.Optional[typing.Sequence[div_background.DivBackground]] = None,
        border: typing.Optional[div_border.DivBorder] = None,
        column_span: typing.Optional[typing.Union[Expr, int]] = None,
        default_state_id: typing.Optional[typing.Union[Expr, str]] = None,
        disappear_actions: typing.Optional[typing.Sequence[div_disappear_action.DivDisappearAction]] = None,
        div_id: typing.Optional[typing.Union[Expr, str]] = None,
        extensions: typing.Optional[typing.Sequence[div_extension.DivExtension]] = None,
        focus: typing.Optional[div_focus.DivFocus] = None,
        height: typing.Optional[div_size.DivSize] = None,
        id: typing.Optional[typing.Union[Expr, str]] = None,
        margins: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        row_span: typing.Optional[typing.Union[Expr, int]] = None,
        selected_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = None,
        states: typing.Optional[typing.Sequence[DivStateState]] = None,
        tooltips: typing.Optional[typing.Sequence[div_tooltip.DivTooltip]] = None,
        transform: typing.Optional[div_transform.DivTransform] = None,
        transition_animation_selector: typing.Optional[typing.Union[Expr, div_transition_selector.DivTransitionSelector]] = None,
        transition_change: typing.Optional[div_change_transition.DivChangeTransition] = None,
        transition_in: typing.Optional[div_appearance_transition.DivAppearanceTransition] = None,
        transition_out: typing.Optional[div_appearance_transition.DivAppearanceTransition] = None,
        transition_triggers: typing.Optional[typing.Sequence[typing.Union[Expr, div_transition_trigger.DivTransitionTrigger]]] = None,
        visibility: typing.Optional[typing.Union[Expr, div_visibility.DivVisibility]] = None,
        visibility_action: typing.Optional[div_visibility_action.DivVisibilityAction] = None,
        visibility_actions: typing.Optional[typing.Sequence[div_visibility_action.DivVisibilityAction]] = None,
        width: typing.Optional[div_size.DivSize] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            accessibility=accessibility,
            alignment_horizontal=alignment_horizontal,
            alignment_vertical=alignment_vertical,
            alpha=alpha,
            background=background,
            border=border,
            column_span=column_span,
            default_state_id=default_state_id,
            disappear_actions=disappear_actions,
            div_id=div_id,
            extensions=extensions,
            focus=focus,
            height=height,
            id=id,
            margins=margins,
            paddings=paddings,
            row_span=row_span,
            selected_actions=selected_actions,
            states=states,
            tooltips=tooltips,
            transform=transform,
            transition_animation_selector=transition_animation_selector,
            transition_change=transition_change,
            transition_in=transition_in,
            transition_out=transition_out,
            transition_triggers=transition_triggers,
            visibility=visibility,
            visibility_action=visibility_action,
            visibility_actions=visibility_actions,
            width=width,
            **kwargs,
        )

    type: str = Field(default="state")
    accessibility: typing.Optional[div_accessibility.DivAccessibility] = Field(
        description="Accessibility settings.",
    )
    alignment_horizontal: typing.Optional[typing.Union[Expr, div_alignment_horizontal.DivAlignmentHorizontal]] = Field(
        description=(
            "Horizontal alignment of an element inside the parent "
            "element."
        ),
    )
    alignment_vertical: typing.Optional[typing.Union[Expr, div_alignment_vertical.DivAlignmentVertical]] = Field(
        description=(
            "Vertical alignment of an element inside the parent element."
        ),
    )
    alpha: typing.Optional[typing.Union[Expr, float]] = Field(
        description=(
            "Sets transparency of the entire element: `0` — completely "
            "transparent, `1` —opaque."
        ),
    )
    background: typing.Optional[typing.Sequence[div_background.DivBackground]] = Field(
        min_items=1, 
        description="Element background. It can contain multiple layers.",
    )
    border: typing.Optional[div_border.DivBorder] = Field(
        description="Element stroke.",
    )
    column_span: typing.Optional[typing.Union[Expr, int]] = Field(
        description=(
            "Merges cells in a column of the [grid](div-grid.md) "
            "element."
        ),
    )
    default_state_id: typing.Optional[typing.Union[Expr, str]] = Field(
        description=(
            "ID of the status that will be set by default. If the "
            "parameter isnt set, thefirst state of the `states` will be "
            "set."
        ),
    )
    disappear_actions: typing.Optional[typing.Sequence[div_disappear_action.DivDisappearAction]] = Field(
        min_items=1, 
        description="Actions when an element disappears from the screen.",
    )
    div_id: typing.Optional[typing.Union[Expr, str]] = Field(
        description=(
            "ID of an element to search in the hierarchy. The ID must be "
            "unique at onehierarchy level. @deprecated"
        ),
    )
    extensions: typing.Optional[typing.Sequence[div_extension.DivExtension]] = Field(
        min_items=1, 
        description=(
            "Extensions for additional processing of an element. The "
            "list of extensions isgiven in "
            "[DivExtension](../../extensions.dita)."
        ),
    )
    focus: typing.Optional[div_focus.DivFocus] = Field(
        description="Parameters when focusing on an element or losing focus.",
    )
    height: typing.Optional[div_size.DivSize] = Field(
        description=(
            "Element height. For Android: if there is text in this or in "
            "a child element,specify height in `sp` to scale the element "
            "together with the text. To learn moreabout units of size "
            "measurement, see [Layout inside the "
            "card](../../layout.dita)."
        ),
    )
    id: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1, 
        description=(
            "Element ID. It must be unique within the root element. It "
            "is used as`accessibilityIdentifier` on iOS."
        ),
    )
    margins: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(
        description="External margins from the element stroke.",
    )
    paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(
        description="Internal margins from the element stroke.",
    )
    row_span: typing.Optional[typing.Union[Expr, int]] = Field(
        description=(
            "Merges cells in a string of the [grid](div-grid.md) "
            "element."
        ),
    )
    selected_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = Field(
        min_items=1, 
        description=(
            "List of [actions](div-action.md) to be executed when "
            "selecting an element in[pager](div-pager.md)."
        ),
    )
    states: typing.Sequence[DivStateState] = Field(
        min_items=1, 
        description=(
            "States. Each element can have a few states with a different "
            "layout. Transitionbetween states is performed using "
            "[special scheme](../../interaction.dita) of "
            "the[action](div-action.md) element."
        ),
    )
    tooltips: typing.Optional[typing.Sequence[div_tooltip.DivTooltip]] = Field(
        min_items=1, 
        description=(
            "Tooltips linked to an element. A tooltip can be shown "
            "by`div-action://show_tooltip?id=`, hidden by "
            "`div-action://hide_tooltip?id=` where`id` — tooltip id."
        ),
    )
    transform: typing.Optional[div_transform.DivTransform] = Field(
        description=(
            "Applies the passed transformation to the element. Content "
            "that doesn\'t fit intothe original view area is cut off."
        ),
    )
    transition_animation_selector: typing.Optional[typing.Union[Expr, div_transition_selector.DivTransitionSelector]] = Field(
        description=(
            "It determines which events trigger transition animations. "
            "@deprecated"
        ),
    )
    transition_change: typing.Optional[div_change_transition.DivChangeTransition] = Field(
        description=(
            "Change animation. It is played when the position or size of "
            "an element changes inthe new layout."
        ),
    )
    transition_in: typing.Optional[div_appearance_transition.DivAppearanceTransition] = Field(
        description=(
            "Appearance animation. It is played when an element with a "
            "new ID appears. Tolearn more about the concept of "
            "transitions, see "
            "[Animatedtransitions](../../interaction.dita#animation/tran"
            "sition-animation)."
        ),
    )
    transition_out: typing.Optional[div_appearance_transition.DivAppearanceTransition] = Field(
        description=(
            "Disappearance animation. It is played when an element "
            "disappears in the newlayout."
        ),
    )
    transition_triggers: typing.Optional[typing.Sequence[typing.Union[Expr, div_transition_trigger.DivTransitionTrigger]]] = Field(
        min_items=1, 
        description=(
            "Animation starting triggers. Default value: `[state_change, "
            "visibility_change]`."
        ),
    )
    visibility: typing.Optional[typing.Union[Expr, div_visibility.DivVisibility]] = Field(
        description="Element visibility.",
    )
    visibility_action: typing.Optional[div_visibility_action.DivVisibilityAction] = Field(
        description=(
            "Tracking visibility of a single element. Not used if the "
            "`visibility_actions`parameter is set."
        ),
    )
    visibility_actions: typing.Optional[typing.Sequence[div_visibility_action.DivVisibilityAction]] = Field(
        min_items=1, 
        description="Actions when an element appears on the screen.",
    )
    width: typing.Optional[div_size.DivSize] = Field(
        description="Element width.",
    )


class DivStateState(BaseDiv):

    def __init__(
        self, *,
        animation_in: typing.Optional[div_animation.DivAnimation] = None,
        animation_out: typing.Optional[div_animation.DivAnimation] = None,
        div: typing.Optional[div.Div] = None,
        state_id: typing.Optional[typing.Union[Expr, str]] = None,
        swipe_out_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            animation_in=animation_in,
            animation_out=animation_out,
            div=div,
            state_id=state_id,
            swipe_out_actions=swipe_out_actions,
            **kwargs,
        )

    animation_in: typing.Optional[div_animation.DivAnimation] = Field(
        description=(
            "State appearance animation. Use `transition_in` instead. "
            "@deprecated"
        ),
    )
    animation_out: typing.Optional[div_animation.DivAnimation] = Field(
        description=(
            "State disappearance animation. Use `transition_out` "
            "instead. @deprecated"
        ),
    )
    div: typing.Optional[div.Div] = Field(
        description=(
            "Contents. If the parameter is missing, the state won\'t be "
            "displayed."
        ),
    )
    state_id: typing.Union[Expr, str] = Field(
        description="State ID. It must be unique at one hierarchy level.",
    )
    swipe_out_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = Field(
        min_items=1, 
        description="Actions when swiping the state horizontally. @deprecated",
    )


DivStateState.update_forward_refs()


DivState.update_forward_refs()
