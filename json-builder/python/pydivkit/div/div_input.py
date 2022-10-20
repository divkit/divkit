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
from . import div_edge_insets
from . import div_extension
from . import div_focus
from . import div_font_family
from . import div_font_weight
from . import div_size
from . import div_size_unit
from . import div_tooltip
from . import div_transform
from . import div_transition_trigger
from . import div_visibility
from . import div_visibility_action


# Text input element.
class DivInput(BaseDiv):

    def __init__(
        self, *,
        text_variable: str,
        type: str = 'input',
        accessibility: typing.Optional[div_accessibility.DivAccessibility] = None,
        alignment_horizontal: typing.Optional[div_alignment_horizontal.DivAlignmentHorizontal] = None,
        alignment_vertical: typing.Optional[div_alignment_vertical.DivAlignmentVertical] = None,
        alpha: typing.Optional[float] = None,
        background: typing.Optional[typing.List[div_background.DivBackground]] = None,
        border: typing.Optional[div_border.DivBorder] = None,
        column_span: typing.Optional[int] = None,
        extensions: typing.Optional[typing.List[div_extension.DivExtension]] = None,
        focus: typing.Optional[div_focus.DivFocus] = None,
        font_family: typing.Optional[div_font_family.DivFontFamily] = None,
        font_size: typing.Optional[int] = None,
        font_size_unit: typing.Optional[div_size_unit.DivSizeUnit] = None,
        font_weight: typing.Optional[div_font_weight.DivFontWeight] = None,
        height: typing.Optional[div_size.DivSize] = None,
        highlight_color: typing.Optional[str] = None,
        hint_color: typing.Optional[str] = None,
        hint_text: typing.Optional[str] = None,
        id: typing.Optional[str] = None,
        keyboard_type: typing.Optional[DivInputKeyboardType] = None,
        letter_spacing: typing.Optional[float] = None,
        line_height: typing.Optional[int] = None,
        margins: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        max_visible_lines: typing.Optional[int] = None,
        native_interface: typing.Optional[DivInputNativeInterface] = None,
        paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        row_span: typing.Optional[int] = None,
        select_all_on_focus: typing.Optional[bool] = None,
        selected_actions: typing.Optional[typing.List[div_action.DivAction]] = None,
        text_color: typing.Optional[str] = None,
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
            font_family=font_family,
            font_size=font_size,
            font_size_unit=font_size_unit,
            font_weight=font_weight,
            height=height,
            highlight_color=highlight_color,
            hint_color=hint_color,
            hint_text=hint_text,
            id=id,
            keyboard_type=keyboard_type,
            letter_spacing=letter_spacing,
            line_height=line_height,
            margins=margins,
            max_visible_lines=max_visible_lines,
            native_interface=native_interface,
            paddings=paddings,
            row_span=row_span,
            select_all_on_focus=select_all_on_focus,
            selected_actions=selected_actions,
            text_color=text_color,
            text_variable=text_variable,
            tooltips=tooltips,
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

    type: str = Field(default='input')
    accessibility: typing.Optional[div_accessibility.DivAccessibility] = Field(description='Accessibility for disabled people.')
    alignment_horizontal: typing.Optional[div_alignment_horizontal.DivAlignmentHorizontal] = Field(description='Horizontal alignment of an element inside the parent element.')
    alignment_vertical: typing.Optional[div_alignment_vertical.DivAlignmentVertical] = Field(description='Vertical alignment of an element inside the parent element.')
    alpha: typing.Optional[float] = Field(description='Sets transparency of the entire element: `0` — completely transparent, `1` —opaque.')
    background: typing.Optional[typing.List[div_background.DivBackground]] = Field(min_items=1, description='Element background. It can contain multiple layers.')
    border: typing.Optional[div_border.DivBorder] = Field(description='Element stroke.')
    column_span: typing.Optional[int] = Field(description='Merges cells in a column of the [grid](div-grid.md) element.')
    extensions: typing.Optional[typing.List[div_extension.DivExtension]] = Field(min_items=1, description='Extensions for additional processing of an element. The list of extensions isgiven in  [DivExtension](../../extensions.dita).')
    focus: typing.Optional[div_focus.DivFocus] = Field(description='Parameters when focusing on an element or losing focus.')
    font_family: typing.Optional[div_font_family.DivFontFamily] = Field(description='Font family:`text` — a standard text font;`display` — a family of fonts with alarge font size.')
    font_size: typing.Optional[int] = Field(description='Font size.')
    font_size_unit: typing.Optional[div_size_unit.DivSizeUnit] = Field(description='Unit of measurement:`px` — a physical pixel.`dp` — a logical pixel that doesn\'tdepend on screen density.`sp` — a logical pixel that depends on the font size ona device. Specify height in `sp`. Only available on Android.')
    font_weight: typing.Optional[div_font_weight.DivFontWeight] = Field(description='Style.')
    height: typing.Optional[div_size.DivSize] = Field(description='Element height. For Android: if there is text in this or in a child element,specify height in `sp` to scale the element together with the text. To learn moreabout units of size measurement, see [Layout inside the card](../../layout.dita).')
    highlight_color: typing.Optional[str] = Field(format="color", description='Text highlight color. If the value isn\'t set, the color set in the client will beused instead.')
    hint_color: typing.Optional[str] = Field(format="color", description='Text color.')
    hint_text: typing.Optional[str] = Field(min_length=1, description='Tooltip text.')
    id: typing.Optional[str] = Field(min_length=1, description='Element ID. It must be unique within the root element. It is used as`accessibilityIdentifier` on iOS.')
    keyboard_type: typing.Optional[DivInputKeyboardType] = Field(description='Keyboard type.')
    letter_spacing: typing.Optional[float] = Field(description='Spacing between characters.')
    line_height: typing.Optional[int] = Field(description='Line spacing of the text range. The count is taken from the font baseline.Measured in units specified in `font_size_unit`.')
    margins: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(description='External margins from the element stroke.')
    max_visible_lines: typing.Optional[int] = Field(description='Maximum number of lines that will be visible in the input view.')
    native_interface: typing.Optional[DivInputNativeInterface] = Field(description='Text input line used in the native interface.')
    paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(description='Internal margins from the element stroke.')
    row_span: typing.Optional[int] = Field(description='Merges cells in a string of the [grid](div-grid.md) element.')
    select_all_on_focus: typing.Optional[bool] = Field(description='Highlighting input text when focused.')
    selected_actions: typing.Optional[typing.List[div_action.DivAction]] = Field(min_items=1, description='List of [actions](div-action.md) to be executed when selecting an element in[pager](div-pager.md).')
    text_color: typing.Optional[str] = Field(format="color", description='Text color.')
    text_variable: str = Field(min_length=1, description='Name of text storage variable.')
    tooltips: typing.Optional[typing.List[div_tooltip.DivTooltip]] = Field(min_items=1, description='Tooltips linked to an element. A tooltip can be shown by`div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where`id` — tooltip id.')
    transform: typing.Optional[div_transform.DivTransform] = Field(description='Transformation of the element. Applies the passed transform to the element. Thecontent that does not fit into the original view will be cut off.')
    transition_change: typing.Optional[div_change_transition.DivChangeTransition] = Field(description='Change animation. It is played when the position or size of an element changes inthe new layout.')
    transition_in: typing.Optional[div_appearance_transition.DivAppearanceTransition] = Field(description='Appearance animation. It is played when an element with a new ID appears. Tolearn more about the concept of transitions, see [Animatedtransitions](../../interaction.dita#animation/transition-animation).')
    transition_out: typing.Optional[div_appearance_transition.DivAppearanceTransition] = Field(description='Disappearance animation. It is played when an element disappears in the newlayout.')
    transition_triggers: typing.Optional[typing.List[div_transition_trigger.DivTransitionTrigger]] = Field(min_items=1, description='Animation starting triggers. Default value: `[state_change, visibility_change]`.')
    visibility: typing.Optional[div_visibility.DivVisibility] = Field(description='Element visibility.')
    visibility_action: typing.Optional[div_visibility_action.DivVisibilityAction] = Field(description='Tracking visibility of a single element. Not used if the `visibility_actions`parameter is set.')
    visibility_actions: typing.Optional[typing.List[div_visibility_action.DivVisibilityAction]] = Field(min_items=1, description='Actions when an element appears on the screen.')
    width: typing.Optional[div_size.DivSize] = Field(description='Element width.')


class DivInputKeyboardType(str, enum.Enum):
    SINGLE_LINE_TEXT = 'single_line_text'
    MULTI_LINE_TEXT = 'multi_line_text'
    PHONE = 'phone'
    NUMBER = 'number'
    EMAIL = 'email'
    URI = 'uri'


# Text input line used in the native interface.
class DivInputNativeInterface(BaseDiv):

    def __init__(
        self, *,
        color: str,
    ):
        super().__init__(
            color=color,
        )

    color: str = Field(format="color", description='Text input line color.')


DivInputNativeInterface.update_forward_refs()


DivInput.update_forward_refs()
