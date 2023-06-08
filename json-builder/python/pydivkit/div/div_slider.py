# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import (
    div_accessibility, div_action, div_alignment_horizontal,
    div_alignment_vertical, div_appearance_transition, div_background,
    div_border, div_change_transition, div_disappear_action, div_drawable,
    div_edge_insets, div_extension, div_focus, div_font_weight, div_point,
    div_size, div_size_unit, div_tooltip, div_transform, div_transition_trigger,
    div_visibility, div_visibility_action,
)


# Slider for selecting a value in the range.
class DivSlider(BaseDiv):

    def __init__(
        self, *,
        type: str = "slider",
        accessibility: typing.Optional[div_accessibility.DivAccessibility] = None,
        alignment_horizontal: typing.Optional[typing.Union[Expr, div_alignment_horizontal.DivAlignmentHorizontal]] = None,
        alignment_vertical: typing.Optional[typing.Union[Expr, div_alignment_vertical.DivAlignmentVertical]] = None,
        alpha: typing.Optional[typing.Union[Expr, float]] = None,
        background: typing.Optional[typing.Sequence[div_background.DivBackground]] = None,
        border: typing.Optional[div_border.DivBorder] = None,
        column_span: typing.Optional[typing.Union[Expr, int]] = None,
        disappear_actions: typing.Optional[typing.Sequence[div_disappear_action.DivDisappearAction]] = None,
        extensions: typing.Optional[typing.Sequence[div_extension.DivExtension]] = None,
        focus: typing.Optional[div_focus.DivFocus] = None,
        height: typing.Optional[div_size.DivSize] = None,
        id: typing.Optional[typing.Union[Expr, str]] = None,
        margins: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        max_value: typing.Optional[typing.Union[Expr, int]] = None,
        min_value: typing.Optional[typing.Union[Expr, int]] = None,
        paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        row_span: typing.Optional[typing.Union[Expr, int]] = None,
        secondary_value_accessibility: typing.Optional[div_accessibility.DivAccessibility] = None,
        selected_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = None,
        thumb_secondary_style: typing.Optional[div_drawable.DivDrawable] = None,
        thumb_secondary_text_style: typing.Optional[DivSliderTextStyle] = None,
        thumb_secondary_value_variable: typing.Optional[typing.Union[Expr, str]] = None,
        thumb_style: typing.Optional[div_drawable.DivDrawable] = None,
        thumb_text_style: typing.Optional[DivSliderTextStyle] = None,
        thumb_value_variable: typing.Optional[typing.Union[Expr, str]] = None,
        tick_mark_active_style: typing.Optional[div_drawable.DivDrawable] = None,
        tick_mark_inactive_style: typing.Optional[div_drawable.DivDrawable] = None,
        tooltips: typing.Optional[typing.Sequence[div_tooltip.DivTooltip]] = None,
        track_active_style: typing.Optional[div_drawable.DivDrawable] = None,
        track_inactive_style: typing.Optional[div_drawable.DivDrawable] = None,
        transform: typing.Optional[div_transform.DivTransform] = None,
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
            disappear_actions=disappear_actions,
            extensions=extensions,
            focus=focus,
            height=height,
            id=id,
            margins=margins,
            max_value=max_value,
            min_value=min_value,
            paddings=paddings,
            row_span=row_span,
            secondary_value_accessibility=secondary_value_accessibility,
            selected_actions=selected_actions,
            thumb_secondary_style=thumb_secondary_style,
            thumb_secondary_text_style=thumb_secondary_text_style,
            thumb_secondary_value_variable=thumb_secondary_value_variable,
            thumb_style=thumb_style,
            thumb_text_style=thumb_text_style,
            thumb_value_variable=thumb_value_variable,
            tick_mark_active_style=tick_mark_active_style,
            tick_mark_inactive_style=tick_mark_inactive_style,
            tooltips=tooltips,
            track_active_style=track_active_style,
            track_inactive_style=track_inactive_style,
            transform=transform,
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

    type: str = Field(default="slider")
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
    disappear_actions: typing.Optional[typing.Sequence[div_disappear_action.DivDisappearAction]] = Field(
        min_items=1, 
        description="Actions when an element disappears from the screen.",
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
    max_value: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Maximum value. It must be greater than the minimum value.",
    )
    min_value: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Minimum value.",
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
    secondary_value_accessibility: typing.Optional[div_accessibility.DivAccessibility] = Field(
        description="Accessibility settings for the second pointer.",
    )
    selected_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = Field(
        min_items=1, 
        description=(
            "List of [actions](div-action.md) to be executed when "
            "selecting an element in[pager](div-pager.md)."
        ),
    )
    thumb_secondary_style: typing.Optional[div_drawable.DivDrawable] = Field(
        description="Style of the second pointer.",
    )
    thumb_secondary_text_style: typing.Optional[DivSliderTextStyle] = Field(
        description="Text style in the second pointer.",
    )
    thumb_secondary_value_variable: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1, 
        description=(
            "Name of the variable to store the second pointer\'s current "
            "value."
        ),
    )
    thumb_style: div_drawable.DivDrawable = Field(
        description="Style of the first pointer.",
    )
    thumb_text_style: typing.Optional[DivSliderTextStyle] = Field(
        description="Text style in the first pointer.",
    )
    thumb_value_variable: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1, 
        description=(
            "Name of the variable to store the pointer\'s current value."
        ),
    )
    tick_mark_active_style: typing.Optional[div_drawable.DivDrawable] = Field(
        description="Style of active serifs.",
    )
    tick_mark_inactive_style: typing.Optional[div_drawable.DivDrawable] = Field(
        description="Style of inactive serifs.",
    )
    tooltips: typing.Optional[typing.Sequence[div_tooltip.DivTooltip]] = Field(
        min_items=1, 
        description=(
            "Tooltips linked to an element. A tooltip can be shown "
            "by`div-action://show_tooltip?id=`, hidden by "
            "`div-action://hide_tooltip?id=` where`id` — tooltip id."
        ),
    )
    track_active_style: div_drawable.DivDrawable = Field(
        description="Style of the active part of a scale.",
    )
    track_inactive_style: div_drawable.DivDrawable = Field(
        description="Style of the inactive part of a scale.",
    )
    transform: typing.Optional[div_transform.DivTransform] = Field(
        description=(
            "Applies the passed transformation to the element. Content "
            "that doesn\'t fit intothe original view area is cut off."
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


class DivSliderTextStyle(BaseDiv):

    def __init__(
        self, *,
        font_size: typing.Optional[typing.Union[Expr, int]] = None,
        font_size_unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = None,
        font_weight: typing.Optional[typing.Union[Expr, div_font_weight.DivFontWeight]] = None,
        offset: typing.Optional[div_point.DivPoint] = None,
        text_color: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            font_size=font_size,
            font_size_unit=font_size_unit,
            font_weight=font_weight,
            offset=offset,
            text_color=text_color,
            **kwargs,
        )

    font_size: typing.Union[Expr, int] = Field(
        description="Font size.",
    )
    font_size_unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = Field(
    )
    font_weight: typing.Optional[typing.Union[Expr, div_font_weight.DivFontWeight]] = Field(
        description="Style.",
    )
    offset: typing.Optional[div_point.DivPoint] = Field(
        description="Shift relative to the center.",
    )
    text_color: typing.Optional[typing.Union[Expr, str]] = Field(
        format="color", 
        description="Text color.",
    )


DivSliderTextStyle.update_forward_refs()


DivSlider.update_forward_refs()
