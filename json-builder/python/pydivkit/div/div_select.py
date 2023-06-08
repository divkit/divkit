# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import (
    div_accessibility, div_action, div_alignment_horizontal,
    div_alignment_vertical, div_appearance_transition, div_background,
    div_border, div_change_transition, div_disappear_action, div_edge_insets,
    div_extension, div_focus, div_font_family, div_font_weight, div_size,
    div_size_unit, div_tooltip, div_transform, div_transition_trigger,
    div_visibility, div_visibility_action,
)


# List of options with only one to be selected.
class DivSelect(BaseDiv):

    def __init__(
        self, *,
        type: str = "select",
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
        font_family: typing.Optional[typing.Union[Expr, div_font_family.DivFontFamily]] = None,
        font_size: typing.Optional[typing.Union[Expr, int]] = None,
        font_size_unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = None,
        font_weight: typing.Optional[typing.Union[Expr, div_font_weight.DivFontWeight]] = None,
        height: typing.Optional[div_size.DivSize] = None,
        hint_color: typing.Optional[typing.Union[Expr, str]] = None,
        hint_text: typing.Optional[typing.Union[Expr, str]] = None,
        id: typing.Optional[typing.Union[Expr, str]] = None,
        letter_spacing: typing.Optional[typing.Union[Expr, float]] = None,
        line_height: typing.Optional[typing.Union[Expr, int]] = None,
        margins: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        options: typing.Optional[typing.Sequence[DivSelectOption]] = None,
        paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        row_span: typing.Optional[typing.Union[Expr, int]] = None,
        selected_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = None,
        text_color: typing.Optional[typing.Union[Expr, str]] = None,
        tooltips: typing.Optional[typing.Sequence[div_tooltip.DivTooltip]] = None,
        transform: typing.Optional[div_transform.DivTransform] = None,
        transition_change: typing.Optional[div_change_transition.DivChangeTransition] = None,
        transition_in: typing.Optional[div_appearance_transition.DivAppearanceTransition] = None,
        transition_out: typing.Optional[div_appearance_transition.DivAppearanceTransition] = None,
        transition_triggers: typing.Optional[typing.Sequence[typing.Union[Expr, div_transition_trigger.DivTransitionTrigger]]] = None,
        value_variable: typing.Optional[typing.Union[Expr, str]] = None,
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
            font_family=font_family,
            font_size=font_size,
            font_size_unit=font_size_unit,
            font_weight=font_weight,
            height=height,
            hint_color=hint_color,
            hint_text=hint_text,
            id=id,
            letter_spacing=letter_spacing,
            line_height=line_height,
            margins=margins,
            options=options,
            paddings=paddings,
            row_span=row_span,
            selected_actions=selected_actions,
            text_color=text_color,
            tooltips=tooltips,
            transform=transform,
            transition_change=transition_change,
            transition_in=transition_in,
            transition_out=transition_out,
            transition_triggers=transition_triggers,
            value_variable=value_variable,
            visibility=visibility,
            visibility_action=visibility_action,
            visibility_actions=visibility_actions,
            width=width,
            **kwargs,
        )

    type: str = Field(default="select")
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
    font_family: typing.Optional[typing.Union[Expr, div_font_family.DivFontFamily]] = Field(
        description=(
            "Font family:`text` — a standard text font;`display` — a "
            "family of fonts with alarge font size."
        ),
    )
    font_size: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Font size.",
    )
    font_size_unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = Field(
        description=(
            "Unit of measurement:`px` — a physical pixel.`dp` — a "
            "logical pixel that doesn\'tdepend on screen density.`sp` — "
            "a logical pixel that depends on the font size ona device. "
            "Specify height in `sp`. Only available on Android."
        ),
    )
    font_weight: typing.Optional[typing.Union[Expr, div_font_weight.DivFontWeight]] = Field(
        description="Style.",
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
    hint_color: typing.Optional[typing.Union[Expr, str]] = Field(
        format="color", 
        description="Hint color.",
    )
    hint_text: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1, 
        description="Hint text.",
    )
    id: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1, 
        description=(
            "Element ID. It must be unique within the root element. It "
            "is used as`accessibilityIdentifier` on iOS."
        ),
    )
    letter_spacing: typing.Optional[typing.Union[Expr, float]] = Field(
        description="Spacing between characters.",
    )
    line_height: typing.Optional[typing.Union[Expr, int]] = Field(
        description=(
            "Line spacing of the text. Measured in units set in "
            "`font_size_unit`."
        ),
    )
    margins: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(
        description="External margins from the element stroke.",
    )
    options: typing.Sequence[DivSelectOption] = Field(
        min_items=1,
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
    text_color: typing.Optional[typing.Union[Expr, str]] = Field(
        format="color", 
        description="Text color.",
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
    value_variable: typing.Union[Expr, str] = Field(
        min_length=1, 
        description=(
            "Name of the variable that stores the selected option value "
            "(`value`)."
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


# List option.
class DivSelectOption(BaseDiv):

    def __init__(
        self, *,
        text: typing.Optional[typing.Union[Expr, str]] = None,
        value: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            text=text,
            value=value,
            **kwargs,
        )

    text: typing.Optional[typing.Union[Expr, str]] = Field(
        description=(
            "Text description of the option displayed in the list. If "
            "not set, `value` is usedinstead."
        ),
    )
    value: typing.Union[Expr, str] = Field(
        description="Value matching the option.",
    )


DivSelectOption.update_forward_refs()


DivSelect.update_forward_refs()
