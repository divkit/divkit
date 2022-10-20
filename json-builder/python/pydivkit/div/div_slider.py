# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_accessibility
from . import div_action
from . import div_alignment_horizontal
from . import div_alignment_vertical
from . import div_appearance_transition
from . import div_background
from . import div_border
from . import div_change_transition
from . import div_drawable
from . import div_edge_insets
from . import div_extension
from . import div_focus
from . import div_font_weight
from . import div_point
from . import div_size
from . import div_size_unit
from . import div_tooltip
from . import div_transform
from . import div_transition_trigger
from . import div_visibility
from . import div_visibility_action


# Slider for selecting a value in the range.
class DivSlider(BaseDiv):

    def __init__(
        self, *,
        thumb_style: div_drawable.DivDrawable,
        track_active_style: div_drawable.DivDrawable,
        track_inactive_style: div_drawable.DivDrawable,
        type: str = 'slider',
        accessibility: typing.Optional[div_accessibility.DivAccessibility] = None,
        alignment_horizontal: typing.Optional[div_alignment_horizontal.DivAlignmentHorizontal] = None,
        alignment_vertical: typing.Optional[div_alignment_vertical.DivAlignmentVertical] = None,
        alpha: typing.Optional[float] = None,
        background: typing.Optional[typing.List[div_background.DivBackground]] = None,
        border: typing.Optional[div_border.DivBorder] = None,
        column_span: typing.Optional[int] = None,
        extensions: typing.Optional[typing.List[div_extension.DivExtension]] = None,
        focus: typing.Optional[div_focus.DivFocus] = None,
        height: typing.Optional[div_size.DivSize] = None,
        id: typing.Optional[str] = None,
        margins: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        max_value: typing.Optional[int] = None,
        min_value: typing.Optional[int] = None,
        paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        row_span: typing.Optional[int] = None,
        secondary_value_accessibility: typing.Optional[div_accessibility.DivAccessibility] = None,
        selected_actions: typing.Optional[typing.List[div_action.DivAction]] = None,
        thumb_secondary_style: typing.Optional[div_drawable.DivDrawable] = None,
        thumb_secondary_text_style: typing.Optional[DivSliderTextStyle] = None,
        thumb_secondary_value_variable: typing.Optional[str] = None,
        thumb_text_style: typing.Optional[DivSliderTextStyle] = None,
        thumb_value_variable: typing.Optional[str] = None,
        tick_mark_active_style: typing.Optional[div_drawable.DivDrawable] = None,
        tick_mark_inactive_style: typing.Optional[div_drawable.DivDrawable] = None,
        tooltips: typing.Optional[typing.List[div_tooltip.DivTooltip]] = None,
        transform: typing.Optional[div_transform.DivTransform] = None,
        transition_change: typing.Optional[div_change_transition.DivChangeTransition] = None,
        transition_in: typing.Optional[div_appearance_transition.DivAppearanceTransition] = None,
        transition_out: typing.Optional[div_appearance_transition.DivAppearanceTransition] = None,
        transition_triggers: typing.Optional[typing.List[div_transition_trigger.DivTransitionTrigger]] = None,
        visibility: typing.Optional[div_visibility.DivVisibility] = None,
        visibility_action: typing.Optional[div_visibility_action.DivVisibilityAction] = None,
        visibility_actions: typing.Optional[typing.List[div_visibility_action.DivVisibilityAction]] = None,
        width: typing.Optional[div_size.DivSize] = None,
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
        )

    type: str = Field(default='slider')
    accessibility: typing.Optional[div_accessibility.DivAccessibility] = Field(description='Accessibility for disabled people.')
    alignment_horizontal: typing.Optional[div_alignment_horizontal.DivAlignmentHorizontal] = Field(description='Horizontal alignment of an element inside the parent element.')
    alignment_vertical: typing.Optional[div_alignment_vertical.DivAlignmentVertical] = Field(description='Vertical alignment of an element inside the parent element.')
    alpha: typing.Optional[float] = Field(description='Sets transparency of the entire element: `0` — completely transparent, `1` —opaque.')
    background: typing.Optional[typing.List[div_background.DivBackground]] = Field(min_items=1, description='Element background. It can contain multiple layers.')
    border: typing.Optional[div_border.DivBorder] = Field(description='Element stroke.')
    column_span: typing.Optional[int] = Field(description='Merges cells in a column of the [grid](div-grid.md) element.')
    extensions: typing.Optional[typing.List[div_extension.DivExtension]] = Field(min_items=1, description='Extensions for additional processing of an element. The list of extensions isgiven in  [DivExtension](../../extensions.dita).')
    focus: typing.Optional[div_focus.DivFocus] = Field(description='Parameters when focusing on an element or losing focus.')
    height: typing.Optional[div_size.DivSize] = Field(description='Element height. For Android: if there is text in this or in a child element,specify height in `sp` to scale the element together with the text. To learn moreabout units of size measurement, see [Layout inside the card](../../layout.dita).')
    id: typing.Optional[str] = Field(min_length=1, description='Element ID. It must be unique within the root element. It is used as`accessibilityIdentifier` on iOS.')
    margins: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(description='External margins from the element stroke.')
    max_value: typing.Optional[int] = Field(description='Maximum value. It must be greater than the minimum value.')
    min_value: typing.Optional[int] = Field(description='Minimum value.')
    paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(description='Internal margins from the element stroke.')
    row_span: typing.Optional[int] = Field(description='Merges cells in a string of the [grid](div-grid.md) element.')
    secondary_value_accessibility: typing.Optional[div_accessibility.DivAccessibility] = Field(description='Accessibility for the secondary thumb.')
    selected_actions: typing.Optional[typing.List[div_action.DivAction]] = Field(min_items=1, description='List of [actions](div-action.md) to be executed when selecting an element in[pager](div-pager.md).')
    thumb_secondary_style: typing.Optional[div_drawable.DivDrawable] = Field(description='Style of the second pointer.')
    thumb_secondary_text_style: typing.Optional[DivSliderTextStyle] = Field(description='Text style in the second pointer.')
    thumb_secondary_value_variable: typing.Optional[str] = Field(min_length=1, description='Name of the variable to store the current value of the secondary thumb.')
    thumb_style: div_drawable.DivDrawable = Field(description='Style of the first pointer.')
    thumb_text_style: typing.Optional[DivSliderTextStyle] = Field(description='Text style in the first pointer.')
    thumb_value_variable: typing.Optional[str] = Field(min_length=1, description='Name of the variable to store the current thumb value.')
    tick_mark_active_style: typing.Optional[div_drawable.DivDrawable] = Field(description='Style of active serifs.')
    tick_mark_inactive_style: typing.Optional[div_drawable.DivDrawable] = Field(description='Style of inactive serifs.')
    tooltips: typing.Optional[typing.List[div_tooltip.DivTooltip]] = Field(min_items=1, description='Tooltips linked to an element. A tooltip can be shown by`div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where`id` — tooltip id.')
    track_active_style: div_drawable.DivDrawable = Field(description='Style of the active part of a scale.')
    track_inactive_style: div_drawable.DivDrawable = Field(description='Style of the inactive part of a scale.')
    transform: typing.Optional[div_transform.DivTransform] = Field(description='Transformation of the element. Applies the passed transform to the element. Thecontent that does not fit into the original view will be cut off.')
    transition_change: typing.Optional[div_change_transition.DivChangeTransition] = Field(description='Change animation. It is played when the position or size of an element changes inthe new layout.')
    transition_in: typing.Optional[div_appearance_transition.DivAppearanceTransition] = Field(description='Appearance animation. It is played when an element with a new ID appears. Tolearn more about the concept of transitions, see [Animatedtransitions](../../interaction.dita#animation/transition-animation).')
    transition_out: typing.Optional[div_appearance_transition.DivAppearanceTransition] = Field(description='Disappearance animation. It is played when an element disappears in the newlayout.')
    transition_triggers: typing.Optional[typing.List[div_transition_trigger.DivTransitionTrigger]] = Field(min_items=1, description='Animation starting triggers. Default value: `[state_change, visibility_change]`.')
    visibility: typing.Optional[div_visibility.DivVisibility] = Field(description='Element visibility.')
    visibility_action: typing.Optional[div_visibility_action.DivVisibilityAction] = Field(description='Tracking visibility of a single element. Not used if the `visibility_actions`parameter is set.')
    visibility_actions: typing.Optional[typing.List[div_visibility_action.DivVisibilityAction]] = Field(min_items=1, description='Actions when an element appears on the screen.')
    width: typing.Optional[div_size.DivSize] = Field(description='Element width.')


class DivSliderTextStyle(BaseDiv):

    def __init__(
        self, *,
        font_size: int,
        font_size_unit: typing.Optional[div_size_unit.DivSizeUnit] = None,
        font_weight: typing.Optional[div_font_weight.DivFontWeight] = None,
        offset: typing.Optional[div_point.DivPoint] = None,
        text_color: typing.Optional[str] = None,
    ):
        super().__init__(
            font_size=font_size,
            font_size_unit=font_size_unit,
            font_weight=font_weight,
            offset=offset,
            text_color=text_color,
        )

    font_size: int = Field(description='Font size.')
    font_size_unit: typing.Optional[div_size_unit.DivSizeUnit] = Field()
    font_weight: typing.Optional[div_font_weight.DivFontWeight] = Field(description='Style.')
    offset: typing.Optional[div_point.DivPoint] = Field(description='Shift relative to the center.')
    text_color: typing.Optional[str] = Field(format="color", description='Text color.')


DivSliderTextStyle.update_forward_refs()


DivSlider.update_forward_refs()
