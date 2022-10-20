# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_accessibility
from . import div_action
from . import div_alignment_horizontal
from . import div_alignment_vertical
from . import div_animation
from . import div_appearance_transition
from . import div_background
from . import div_border
from . import div_change_transition
from . import div_edge_insets
from . import div_extension
from . import div_fixed_size
from . import div_focus
from . import div_font_family
from . import div_font_weight
from . import div_line_style
from . import div_size
from . import div_size_unit
from . import div_text_gradient
from . import div_tooltip
from . import div_transform
from . import div_transition_trigger
from . import div_visibility
from . import div_visibility_action


# Text.
class DivText(BaseDiv):

    def __init__(
        self, *,
        text: str,
        type: str = 'text',
        accessibility: typing.Optional[div_accessibility.DivAccessibility] = None,
        action: typing.Optional[div_action.DivAction] = None,
        action_animation: typing.Optional[div_animation.DivAnimation] = None,
        actions: typing.Optional[typing.List[div_action.DivAction]] = None,
        alignment_horizontal: typing.Optional[div_alignment_horizontal.DivAlignmentHorizontal] = None,
        alignment_vertical: typing.Optional[div_alignment_vertical.DivAlignmentVertical] = None,
        alpha: typing.Optional[float] = None,
        auto_ellipsize: typing.Optional[bool] = None,
        background: typing.Optional[typing.List[div_background.DivBackground]] = None,
        border: typing.Optional[div_border.DivBorder] = None,
        column_span: typing.Optional[int] = None,
        doubletap_actions: typing.Optional[typing.List[div_action.DivAction]] = None,
        ellipsis: typing.Optional[DivTextEllipsis] = None,
        extensions: typing.Optional[typing.List[div_extension.DivExtension]] = None,
        focus: typing.Optional[div_focus.DivFocus] = None,
        focused_text_color: typing.Optional[str] = None,
        font_family: typing.Optional[div_font_family.DivFontFamily] = None,
        font_size: typing.Optional[int] = None,
        font_size_unit: typing.Optional[div_size_unit.DivSizeUnit] = None,
        font_weight: typing.Optional[div_font_weight.DivFontWeight] = None,
        height: typing.Optional[div_size.DivSize] = None,
        id: typing.Optional[str] = None,
        images: typing.Optional[typing.List[DivTextImage]] = None,
        letter_spacing: typing.Optional[float] = None,
        line_height: typing.Optional[int] = None,
        longtap_actions: typing.Optional[typing.List[div_action.DivAction]] = None,
        margins: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        max_lines: typing.Optional[int] = None,
        min_hidden_lines: typing.Optional[int] = None,
        paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        ranges: typing.Optional[typing.List[DivTextRange]] = None,
        row_span: typing.Optional[int] = None,
        selectable: typing.Optional[bool] = None,
        selected_actions: typing.Optional[typing.List[div_action.DivAction]] = None,
        strike: typing.Optional[div_line_style.DivLineStyle] = None,
        text_alignment_horizontal: typing.Optional[div_alignment_horizontal.DivAlignmentHorizontal] = None,
        text_alignment_vertical: typing.Optional[div_alignment_vertical.DivAlignmentVertical] = None,
        text_color: typing.Optional[str] = None,
        text_gradient: typing.Optional[div_text_gradient.DivTextGradient] = None,
        tooltips: typing.Optional[typing.List[div_tooltip.DivTooltip]] = None,
        transform: typing.Optional[div_transform.DivTransform] = None,
        transition_change: typing.Optional[div_change_transition.DivChangeTransition] = None,
        transition_in: typing.Optional[div_appearance_transition.DivAppearanceTransition] = None,
        transition_out: typing.Optional[div_appearance_transition.DivAppearanceTransition] = None,
        transition_triggers: typing.Optional[typing.List[div_transition_trigger.DivTransitionTrigger]] = None,
        truncate: typing.Optional[DivTextTruncate] = None,
        underline: typing.Optional[div_line_style.DivLineStyle] = None,
        visibility: typing.Optional[div_visibility.DivVisibility] = None,
        visibility_action: typing.Optional[div_visibility_action.DivVisibilityAction] = None,
        visibility_actions: typing.Optional[typing.List[div_visibility_action.DivVisibilityAction]] = None,
        width: typing.Optional[div_size.DivSize] = None,
    ):
        super().__init__(
            type=type,
            accessibility=accessibility,
            action=action,
            action_animation=action_animation,
            actions=actions,
            alignment_horizontal=alignment_horizontal,
            alignment_vertical=alignment_vertical,
            alpha=alpha,
            auto_ellipsize=auto_ellipsize,
            background=background,
            border=border,
            column_span=column_span,
            doubletap_actions=doubletap_actions,
            ellipsis=ellipsis,
            extensions=extensions,
            focus=focus,
            focused_text_color=focused_text_color,
            font_family=font_family,
            font_size=font_size,
            font_size_unit=font_size_unit,
            font_weight=font_weight,
            height=height,
            id=id,
            images=images,
            letter_spacing=letter_spacing,
            line_height=line_height,
            longtap_actions=longtap_actions,
            margins=margins,
            max_lines=max_lines,
            min_hidden_lines=min_hidden_lines,
            paddings=paddings,
            ranges=ranges,
            row_span=row_span,
            selectable=selectable,
            selected_actions=selected_actions,
            strike=strike,
            text=text,
            text_alignment_horizontal=text_alignment_horizontal,
            text_alignment_vertical=text_alignment_vertical,
            text_color=text_color,
            text_gradient=text_gradient,
            tooltips=tooltips,
            transform=transform,
            transition_change=transition_change,
            transition_in=transition_in,
            transition_out=transition_out,
            transition_triggers=transition_triggers,
            truncate=truncate,
            underline=underline,
            visibility=visibility,
            visibility_action=visibility_action,
            visibility_actions=visibility_actions,
            width=width,
        )

    type: str = Field(default='text')
    accessibility: typing.Optional[div_accessibility.DivAccessibility] = Field(description='Accessibility for disabled people.')
    action: typing.Optional[div_action.DivAction] = Field(description='One action when clicking on an element. Not used if the `actions` parameter isset.')
    action_animation: typing.Optional[div_animation.DivAnimation] = Field(description='Action animation. Web supports `fade`, `scale` and `set` only.')
    actions: typing.Optional[typing.List[div_action.DivAction]] = Field(min_items=1, description='Multiple actions when clicking on an element.')
    alignment_horizontal: typing.Optional[div_alignment_horizontal.DivAlignmentHorizontal] = Field(description='Horizontal alignment of an element inside the parent element.')
    alignment_vertical: typing.Optional[div_alignment_vertical.DivAlignmentVertical] = Field(description='Vertical alignment of an element inside the parent element.')
    alpha: typing.Optional[float] = Field(description='Sets transparency of the entire element: `0` — completely transparent, `1` —opaque.')
    auto_ellipsize: typing.Optional[bool] = Field(description='Automatic text cropping to fit the container size.')
    background: typing.Optional[typing.List[div_background.DivBackground]] = Field(min_items=1, description='Element background. It can contain multiple layers.')
    border: typing.Optional[div_border.DivBorder] = Field(description='Element stroke.')
    column_span: typing.Optional[int] = Field(description='Merges cells in a column of the [grid](div-grid.md) element.')
    doubletap_actions: typing.Optional[typing.List[div_action.DivAction]] = Field(min_items=1, description='Action when double-clicking on an element.')
    ellipsis: typing.Optional[DivTextEllipsis] = Field(description='Text cropping marker. It is displayed when text size exceeds the limit on thenumber of lines.')
    extensions: typing.Optional[typing.List[div_extension.DivExtension]] = Field(min_items=1, description='Extensions for additional processing of an element. The list of extensions isgiven in  [DivExtension](../../extensions.dita).')
    focus: typing.Optional[div_focus.DivFocus] = Field(description='Parameters when focusing on an element or losing focus.')
    focused_text_color: typing.Optional[str] = Field(format="color", description='Text color when focusing on the element.')
    font_family: typing.Optional[div_font_family.DivFontFamily] = Field(description='Font family:`text` — a standard text font;`display` — a family of fonts with alarge font size.')
    font_size: typing.Optional[int] = Field(description='Font size.')
    font_size_unit: typing.Optional[div_size_unit.DivSizeUnit] = Field()
    font_weight: typing.Optional[div_font_weight.DivFontWeight] = Field(description='Style.')
    height: typing.Optional[div_size.DivSize] = Field(description='Element height. For Android: if there is text in this or in a child element,specify height in `sp` to scale the element together with the text. To learn moreabout units of size measurement, see [Layout inside the card](../../layout.dita).')
    id: typing.Optional[str] = Field(min_length=1, description='Element ID. It must be unique within the root element. It is used as`accessibilityIdentifier` on iOS.')
    images: typing.Optional[typing.List[DivTextImage]] = Field(min_items=1, description='Images embedded in text.')
    letter_spacing: typing.Optional[float] = Field(description='Spacing between characters.')
    line_height: typing.Optional[int] = Field(description='Line spacing of the text range. The count is taken from the font baseline.')
    longtap_actions: typing.Optional[typing.List[div_action.DivAction]] = Field(min_items=1, description='Action when long-clicking on an element.')
    margins: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(description='External margins from the element stroke.')
    max_lines: typing.Optional[int] = Field(description='Maximum number of lines not to be cropped when breaking the limits.')
    min_hidden_lines: typing.Optional[int] = Field(description='Minimum number of cropped lines when breaking the limits.')
    paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(description='Internal margins from the element stroke.')
    ranges: typing.Optional[typing.List[DivTextRange]] = Field(min_items=1, description='A character range in which additional style parameters can be set. Defined bymandatory `start` and `end` fields.')
    row_span: typing.Optional[int] = Field(description='Merges cells in a string of the [grid](div-grid.md) element.')
    selectable: typing.Optional[bool] = Field(description='Selecting and copying text.')
    selected_actions: typing.Optional[typing.List[div_action.DivAction]] = Field(min_items=1, description='List of [actions](div-action.md) to be executed when selecting an element in[pager](div-pager.md).')
    strike: typing.Optional[div_line_style.DivLineStyle] = Field(description='Strikethrough.')
    text: str = Field(min_length=1, description='Text.')
    text_alignment_horizontal: typing.Optional[div_alignment_horizontal.DivAlignmentHorizontal] = Field(description='Horizontal text alignment.')
    text_alignment_vertical: typing.Optional[div_alignment_vertical.DivAlignmentVertical] = Field(description='Vertical text alignment.')
    text_color: typing.Optional[str] = Field(format="color", description='Text color. Not used if the `text_gradient` parameter is set.')
    text_gradient: typing.Optional[div_text_gradient.DivTextGradient] = Field(description='Gradient text color.')
    tooltips: typing.Optional[typing.List[div_tooltip.DivTooltip]] = Field(min_items=1, description='Tooltips linked to an element. A tooltip can be shown by`div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where`id` — tooltip id.')
    transform: typing.Optional[div_transform.DivTransform] = Field(description='Transformation of the element. Applies the passed transform to the element. Thecontent that does not fit into the original view will be cut off.')
    transition_change: typing.Optional[div_change_transition.DivChangeTransition] = Field(description='Change animation. It is played when the position or size of an element changes inthe new layout.')
    transition_in: typing.Optional[div_appearance_transition.DivAppearanceTransition] = Field(description='Appearance animation. It is played when an element with a new ID appears. Tolearn more about the concept of transitions, see [Animatedtransitions](../../interaction.dita#animation/transition-animation).')
    transition_out: typing.Optional[div_appearance_transition.DivAppearanceTransition] = Field(description='Disappearance animation. It is played when an element disappears in the newlayout.')
    transition_triggers: typing.Optional[typing.List[div_transition_trigger.DivTransitionTrigger]] = Field(min_items=1, description='Animation starting triggers. Default value: `[state_change, visibility_change]`.')
    truncate: typing.Optional[DivTextTruncate] = Field(description='Text cropping method. Use `ellipsis` instead. @deprecated')
    underline: typing.Optional[div_line_style.DivLineStyle] = Field(description='Underline.')
    visibility: typing.Optional[div_visibility.DivVisibility] = Field(description='Element visibility.')
    visibility_action: typing.Optional[div_visibility_action.DivVisibilityAction] = Field(description='Tracking visibility of a single element. Not used if the `visibility_actions`parameter is set.')
    visibility_actions: typing.Optional[typing.List[div_visibility_action.DivVisibilityAction]] = Field(min_items=1, description='Actions when an element appears on the screen.')
    width: typing.Optional[div_size.DivSize] = Field(description='Element width.')


class DivTextTruncate(str, enum.Enum):
    NONE = 'none'
    START = 'start'
    END = 'end'
    MIDDLE = 'middle'


# Text cropping marker. It is displayed when text size exceeds the limit on the
# number of lines.
class DivTextEllipsis(BaseDiv):

    def __init__(
        self, *,
        text: str,
        actions: typing.Optional[typing.List[div_action.DivAction]] = None,
        images: typing.Optional[typing.List[DivTextImage]] = None,
        ranges: typing.Optional[typing.List[DivTextRange]] = None,
    ):
        super().__init__(
            actions=actions,
            images=images,
            ranges=ranges,
            text=text,
        )

    actions: typing.Optional[typing.List[div_action.DivAction]] = Field(min_items=1, description='Actions when clicking on a crop marker.')
    images: typing.Optional[typing.List[DivTextImage]] = Field(min_items=1, description='Images embedded in a crop marker.')
    ranges: typing.Optional[typing.List[DivTextRange]] = Field(min_items=1, description='Character ranges inside a crop marker with different text styles.')
    text: str = Field(min_length=1, description='Marker text.')


DivTextEllipsis.update_forward_refs()


# Image.
class DivTextImage(BaseDiv):

    def __init__(
        self, *,
        start: int,
        url: str,
        height: typing.Optional[div_fixed_size.DivFixedSize] = None,
        tint_color: typing.Optional[str] = None,
        width: typing.Optional[div_fixed_size.DivFixedSize] = None,
    ):
        super().__init__(
            height=height,
            start=start,
            tint_color=tint_color,
            url=url,
            width=width,
        )

    height: typing.Optional[div_fixed_size.DivFixedSize] = Field(description='Image height.')
    start: int = Field(description='A symbol to insert prior to an image. To insert an image at the end of the text,specify the number of the last character plus one.')
    tint_color: typing.Optional[str] = Field(format="color", description='New color of a contour image.')
    url: str = Field(format="uri", description='Image URL.')
    width: typing.Optional[div_fixed_size.DivFixedSize] = Field(description='Image width.')


DivTextImage.update_forward_refs()


# Additional parameters of the character range.
class DivTextRange(BaseDiv):

    def __init__(
        self, *,
        end: int,
        start: int,
        actions: typing.Optional[typing.List[div_action.DivAction]] = None,
        font_family: typing.Optional[div_font_family.DivFontFamily] = None,
        font_size: typing.Optional[int] = None,
        font_size_unit: typing.Optional[div_size_unit.DivSizeUnit] = None,
        font_weight: typing.Optional[div_font_weight.DivFontWeight] = None,
        letter_spacing: typing.Optional[float] = None,
        line_height: typing.Optional[int] = None,
        strike: typing.Optional[div_line_style.DivLineStyle] = None,
        text_color: typing.Optional[str] = None,
        top_offset: typing.Optional[int] = None,
        underline: typing.Optional[div_line_style.DivLineStyle] = None,
    ):
        super().__init__(
            actions=actions,
            end=end,
            font_family=font_family,
            font_size=font_size,
            font_size_unit=font_size_unit,
            font_weight=font_weight,
            letter_spacing=letter_spacing,
            line_height=line_height,
            start=start,
            strike=strike,
            text_color=text_color,
            top_offset=top_offset,
            underline=underline,
        )

    actions: typing.Optional[typing.List[div_action.DivAction]] = Field(min_items=1, description='Action when clicking on text.')
    end: int = Field(description='Ordinal number of the last character to be included in the range.')
    font_family: typing.Optional[div_font_family.DivFontFamily] = Field(description='Font family:`text` — a standard text font;`display` — a family of fonts with alarge font size.')
    font_size: typing.Optional[int] = Field(description='Font size.')
    font_size_unit: typing.Optional[div_size_unit.DivSizeUnit] = Field(description='Unit of measurement:`px` — a physical pixel.`dp` — a logical pixel that doesn\'tdepend on screen density.`sp` — a logical pixel that depends on the font size ona device. Specify height in `sp`. Only available on Android.')
    font_weight: typing.Optional[div_font_weight.DivFontWeight] = Field(description='Style.')
    letter_spacing: typing.Optional[float] = Field(description='Spacing between characters.')
    line_height: typing.Optional[int] = Field(description='Line spacing of the text range. The count is taken from the font baseline.Measured in units specified in `font_size_unit`.')
    start: int = Field(description='Ordinal number of a character which the range begins from. The first characterhas a number `0`.')
    strike: typing.Optional[div_line_style.DivLineStyle] = Field(description='Strikethrough.')
    text_color: typing.Optional[str] = Field(format="color", description='Text color.')
    top_offset: typing.Optional[int] = Field(description='The top margin of the text range. Measured in units specified in`font_size_unit`.')
    underline: typing.Optional[div_line_style.DivLineStyle] = Field(description='Underline.')


DivTextRange.update_forward_refs()


DivText.update_forward_refs()
