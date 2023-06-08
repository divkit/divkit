# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import (
    div, div_accessibility, div_action, div_alignment_horizontal,
    div_alignment_vertical, div_animation, div_appearance_transition,
    div_aspect, div_background, div_border, div_change_transition,
    div_disappear_action, div_drawable, div_edge_insets, div_extension,
    div_focus, div_size, div_tooltip, div_transform, div_transition_trigger,
    div_visibility, div_visibility_action,
)


# Container. It contains other elements and is responsible for their location. It
# is used to arrange elements vertically, horizontally, and with an overlay in a
# certain order, simulating the third dimension.
class DivContainer(BaseDiv):

    def __init__(
        self, *,
        type: str = "container",
        accessibility: typing.Optional[div_accessibility.DivAccessibility] = None,
        action: typing.Optional[div_action.DivAction] = None,
        action_animation: typing.Optional[div_animation.DivAnimation] = None,
        actions: typing.Optional[typing.Sequence[div_action.DivAction]] = None,
        alignment_horizontal: typing.Optional[typing.Union[Expr, div_alignment_horizontal.DivAlignmentHorizontal]] = None,
        alignment_vertical: typing.Optional[typing.Union[Expr, div_alignment_vertical.DivAlignmentVertical]] = None,
        alpha: typing.Optional[typing.Union[Expr, float]] = None,
        aspect: typing.Optional[div_aspect.DivAspect] = None,
        background: typing.Optional[typing.Sequence[div_background.DivBackground]] = None,
        border: typing.Optional[div_border.DivBorder] = None,
        column_span: typing.Optional[typing.Union[Expr, int]] = None,
        content_alignment_horizontal: typing.Optional[typing.Union[Expr, div_alignment_horizontal.DivAlignmentHorizontal]] = None,
        content_alignment_vertical: typing.Optional[typing.Union[Expr, div_alignment_vertical.DivAlignmentVertical]] = None,
        disappear_actions: typing.Optional[typing.Sequence[div_disappear_action.DivDisappearAction]] = None,
        doubletap_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = None,
        extensions: typing.Optional[typing.Sequence[div_extension.DivExtension]] = None,
        focus: typing.Optional[div_focus.DivFocus] = None,
        height: typing.Optional[div_size.DivSize] = None,
        id: typing.Optional[typing.Union[Expr, str]] = None,
        items: typing.Optional[typing.Sequence[div.Div]] = None,
        layout_mode: typing.Optional[typing.Union[Expr, DivContainerLayoutMode]] = None,
        line_separator: typing.Optional[DivContainerSeparator] = None,
        longtap_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = None,
        margins: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        orientation: typing.Optional[typing.Union[Expr, DivContainerOrientation]] = None,
        paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        row_span: typing.Optional[typing.Union[Expr, int]] = None,
        selected_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = None,
        separator: typing.Optional[DivContainerSeparator] = None,
        tooltips: typing.Optional[typing.Sequence[div_tooltip.DivTooltip]] = None,
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
            action=action,
            action_animation=action_animation,
            actions=actions,
            alignment_horizontal=alignment_horizontal,
            alignment_vertical=alignment_vertical,
            alpha=alpha,
            aspect=aspect,
            background=background,
            border=border,
            column_span=column_span,
            content_alignment_horizontal=content_alignment_horizontal,
            content_alignment_vertical=content_alignment_vertical,
            disappear_actions=disappear_actions,
            doubletap_actions=doubletap_actions,
            extensions=extensions,
            focus=focus,
            height=height,
            id=id,
            items=items,
            layout_mode=layout_mode,
            line_separator=line_separator,
            longtap_actions=longtap_actions,
            margins=margins,
            orientation=orientation,
            paddings=paddings,
            row_span=row_span,
            selected_actions=selected_actions,
            separator=separator,
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
            **kwargs,
        )

    type: str = Field(default="container")
    accessibility: typing.Optional[div_accessibility.DivAccessibility] = Field(
        description="Accessibility settings.",
    )
    action: typing.Optional[div_action.DivAction] = Field(
        description=(
            "One action when clicking on an element. Not used if the "
            "`actions` parameter isset."
        ),
    )
    action_animation: typing.Optional[div_animation.DivAnimation] = Field(
        description=(
            "Click animation. The web only supports the following "
            "values: `fade`, `scale`,`native`, `no_animation` and `set`."
        ),
    )
    actions: typing.Optional[typing.Sequence[div_action.DivAction]] = Field(
        min_items=1, 
        description="Multiple actions when clicking on an element.",
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
    aspect: typing.Optional[div_aspect.DivAspect] = Field(
        description=(
            "Fixed aspect ratio of the container. The element\'s height "
            "is calculated based onthe width, ignoring the `height` "
            "parameter\'s value. On the web, support for "
            "the`aspect-ratio` CSS property is required to use this "
            "parameter."
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
    content_alignment_horizontal: typing.Optional[typing.Union[Expr, div_alignment_horizontal.DivAlignmentHorizontal]] = Field(
        description=(
            "Horizontal element alignment. For child elements, it can be "
            "redefined using the`alignment_horizontal` property."
        ),
    )
    content_alignment_vertical: typing.Optional[typing.Union[Expr, div_alignment_vertical.DivAlignmentVertical]] = Field(
        description=(
            "Vertical element alignment. The `baseline` value aligns "
            "elements along their ownspecified baseline (for text and "
            "other elements that have a baseline). Elementsthat don\'t "
            "have their baseline value specified are aligned along the "
            "top edge.For child elements, it can be redefined using the "
            "`alignment_vertical` property."
        ),
    )
    disappear_actions: typing.Optional[typing.Sequence[div_disappear_action.DivDisappearAction]] = Field(
        min_items=1, 
        description="Actions when an element disappears from the screen.",
    )
    doubletap_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = Field(
        min_items=1, 
        description="Action when double-clicking on an element.",
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
    items: typing.Sequence[div.Div] = Field(
        min_items=1, 
        description="Nested elements.",
    )
    layout_mode: typing.Optional[typing.Union[Expr, DivContainerLayoutMode]] = Field(
        description=(
            "Element placement method. The `wrap` value transfers "
            "elements to the next line ifthey don\'t fit in the previous "
            "one. If the `wrap` value is set:A separate line isallocated "
            "for each element along the main axis with the size value "
            "set to`match_parent`.Elements along the cross axis with the "
            "size value `match_parent`are ignored."
        ),
    )
    line_separator: typing.Optional[DivContainerSeparator] = Field(
        description=(
            "Separator between elements along the cross axis. Not used "
            "if the `layout_mode`parameter is set to `no_wrap`. Only new "
            "browsers are supported on the web (the`gap` property must "
            "be supported for flex blocks)."
        ),
    )
    longtap_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = Field(
        min_items=1, 
        description=(
            "Action when long-clicking an element. Doesn\'t work on "
            "devices that don\'t supporttouch gestures."
        ),
    )
    margins: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(
        description="External margins from the element stroke.",
    )
    orientation: typing.Optional[typing.Union[Expr, DivContainerOrientation]] = Field(
        description=(
            "Location of elements. `overlap` value overlays elements on "
            "top of each other inthe order of enumeration. The lowest is "
            "the zero element of an array."
        ),
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
    separator: typing.Optional[DivContainerSeparator] = Field(
        description=(
            "Separator between elements along the main axis. Not used if "
            "the `orientation`parameter is set to `overlap`. Only new "
            "browsers are supported on the web (the`gap` property must "
            "be supported for flex blocks)."
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


class DivContainerLayoutMode(str, enum.Enum):
    NO_WRAP = "no_wrap"
    WRAP = "wrap"


class DivContainerOrientation(str, enum.Enum):
    VERTICAL = "vertical"
    HORIZONTAL = "horizontal"
    OVERLAP = "overlap"


class DivContainerSeparator(BaseDiv):

    def __init__(
        self, *,
        show_at_end: typing.Optional[typing.Union[Expr, bool]] = None,
        show_at_start: typing.Optional[typing.Union[Expr, bool]] = None,
        show_between: typing.Optional[typing.Union[Expr, bool]] = None,
        style: typing.Optional[div_drawable.DivDrawable] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            show_at_end=show_at_end,
            show_at_start=show_at_start,
            show_between=show_between,
            style=style,
            **kwargs,
        )

    show_at_end: typing.Optional[typing.Union[Expr, bool]] = Field(
        description="Enables displaying the separator after the last item.",
    )
    show_at_start: typing.Optional[typing.Union[Expr, bool]] = Field(
        description="Enables displaying the separator before the first item.",
    )
    show_between: typing.Optional[typing.Union[Expr, bool]] = Field(
        description="Enables displaying the separator between items.",
    )
    style: div_drawable.DivDrawable = Field(
        description="Separator style.",
    )


DivContainerSeparator.update_forward_refs()


DivContainer.update_forward_refs()
