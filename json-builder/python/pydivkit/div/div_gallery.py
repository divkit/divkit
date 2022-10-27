# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import (
    div, div_accessibility, div_action, div_alignment_horizontal,
    div_alignment_vertical, div_appearance_transition, div_background,
    div_border, div_change_transition, div_edge_insets, div_extension,
    div_focus, div_size, div_tooltip, div_transform, div_transition_trigger,
    div_visibility, div_visibility_action,
)


# Gallery. It contains a horizontal or vertical set of cards that can be scrolled.
class DivGallery(BaseDiv):

    def __init__(
        self, *,
        items: typing.List[div.Div],
        type: str = "gallery",
        accessibility: typing.Optional[div_accessibility.DivAccessibility] = None,
        alignment_horizontal: typing.Optional[div_alignment_horizontal.DivAlignmentHorizontal] = None,
        alignment_vertical: typing.Optional[div_alignment_vertical.DivAlignmentVertical] = None,
        alpha: typing.Optional[float] = None,
        background: typing.Optional[typing.List[div_background.DivBackground]] = None,
        border: typing.Optional[div_border.DivBorder] = None,
        column_count: typing.Optional[int] = None,
        column_span: typing.Optional[int] = None,
        cross_content_alignment: typing.Optional[DivGalleryCrossContentAlignment] = None,
        cross_spacing: typing.Optional[int] = None,
        default_item: typing.Optional[int] = None,
        extensions: typing.Optional[typing.List[div_extension.DivExtension]] = None,
        focus: typing.Optional[div_focus.DivFocus] = None,
        height: typing.Optional[div_size.DivSize] = None,
        id: typing.Optional[str] = None,
        item_spacing: typing.Optional[int] = None,
        margins: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        orientation: typing.Optional[DivGalleryOrientation] = None,
        paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        restrict_parent_scroll: typing.Optional[bool] = None,
        row_span: typing.Optional[int] = None,
        scroll_mode: typing.Optional[DivGalleryScrollMode] = None,
        selected_actions: typing.Optional[typing.List[div_action.DivAction]] = None,
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
            column_count=column_count,
            column_span=column_span,
            cross_content_alignment=cross_content_alignment,
            cross_spacing=cross_spacing,
            default_item=default_item,
            extensions=extensions,
            focus=focus,
            height=height,
            id=id,
            item_spacing=item_spacing,
            items=items,
            margins=margins,
            orientation=orientation,
            paddings=paddings,
            restrict_parent_scroll=restrict_parent_scroll,
            row_span=row_span,
            scroll_mode=scroll_mode,
            selected_actions=selected_actions,
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

    type: str = Field(default="gallery")
    accessibility: typing.Optional[div_accessibility.DivAccessibility] = Field(
        description="Accessibility for disabled people.",
    )
    alignment_horizontal: typing.Optional[div_alignment_horizontal.DivAlignmentHorizontal] = Field(
        description=(
            "Horizontal alignment of an element inside the parent "
            "element."
        ),
    )
    alignment_vertical: typing.Optional[div_alignment_vertical.DivAlignmentVertical] = Field(
        description=(
            "Vertical alignment of an element inside the parent element."
        ),
    )
    alpha: typing.Optional[float] = Field(
        description=(
            "Sets transparency of the entire element: `0` — completely "
            "transparent, `1` —opaque."
        ),
    )
    background: typing.Optional[typing.List[div_background.DivBackground]] = Field(
        min_items=1, 
        description="Element background. It can contain multiple layers.",
    )
    border: typing.Optional[div_border.DivBorder] = Field(
        description="Element stroke.",
    )
    column_count: typing.Optional[int] = Field(
        description="Number of columns for block layout.",
    )
    column_span: typing.Optional[int] = Field(
        description=(
            "Merges cells in a column of the [grid](div-grid.md) "
            "element."
        ),
    )
    cross_content_alignment: typing.Optional[DivGalleryCrossContentAlignment] = Field(
        description=(
            "Aligning elements in the direction perpendicular to the "
            "scroll direction. Inhorizontal galleries:`start` — "
            "alignment to the top of the card;`center` — to "
            "thecenter;`end` — to the bottom.</p><p>In vertical "
            "galleries:`start` — alignment tothe left of the "
            "card;`center` — to the center;`end` — to the right."
        ),
    )
    cross_spacing: typing.Optional[int] = Field(
        description=(
            "Spacing between elements across the scroll axis. Default "
            "value is `item_spacing`"
        ),
    )
    default_item: typing.Optional[int] = Field(
        description=(
            "Ordinal number of the gallery element to be scrolled to by "
            "default. For`scroll_mode`:`default` — the scroll position "
            "is set to the beginning of theelement, without taking into "
            "account `item_spacing`;`paging` — the scrollposition is set "
            "to the center of the element."
        ),
    )
    extensions: typing.Optional[typing.List[div_extension.DivExtension]] = Field(
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
    id: typing.Optional[str] = Field(
        min_length=1, 
        description=(
            "Element ID. It must be unique within the root element. It "
            "is used as`accessibilityIdentifier` on iOS."
        ),
    )
    item_spacing: typing.Optional[int] = Field(
        description="Spacing between elements.",
    )
    items: typing.List[div.Div] = Field(
        min_items=1, 
        description=(
            "Gallery elements. Scrolling to elements can be "
            "implementedusing:`div-action://set_current_item?id=&item=` "
            "— scrolling to the element withan ordinal number `item` "
            "inside an element, with the "
            "specified`id`;`div-action://set_next_item?id=[&overflow={cl"
            "amp|ring}]` — scrolling to thenext element inside an "
            "element, with the "
            "specified`id`;`div-action://set_previous_item?id=[&overflow"
            "={clamp|ring}]` — scrolling tothe previous element inside "
            "an element, with the specified `id`.</p><p>Theoptional "
            "`overflow` parameter is used to set navigation when the "
            "first or lastelement is reached:`clamp` — transition will "
            "stop at the border element;`ring` —go to the beginning or "
            "end, depending on the current element.</p><p>By "
            "default,`clamp`."
        ),
    )
    margins: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(
        description="External margins from the element stroke.",
    )
    orientation: typing.Optional[DivGalleryOrientation] = Field(
        description="Gallery orientation.",
    )
    paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(
        description="Internal margins from the element stroke.",
    )
    restrict_parent_scroll: typing.Optional[bool] = Field(
        description=(
            "If the parameter is enabled, the gallery won\'t transmit "
            "the scroll gesture to theparent element."
        ),
    )
    row_span: typing.Optional[int] = Field(
        description=(
            "Merges cells in a string of the [grid](div-grid.md) "
            "element."
        ),
    )
    scroll_mode: typing.Optional[DivGalleryScrollMode] = Field(
        description=(
            "Scroll type: `default` — continuous, `paging` — "
            "page-by-page."
        ),
    )
    selected_actions: typing.Optional[typing.List[div_action.DivAction]] = Field(
        min_items=1, 
        description=(
            "List of [actions](div-action.md) to be executed when "
            "selecting an element in[pager](div-pager.md)."
        ),
    )
    tooltips: typing.Optional[typing.List[div_tooltip.DivTooltip]] = Field(
        min_items=1, 
        description=(
            "Tooltips linked to an element. A tooltip can be shown "
            "by`div-action://show_tooltip?id=`, hidden by "
            "`div-action://hide_tooltip?id=` where`id` — tooltip id."
        ),
    )
    transform: typing.Optional[div_transform.DivTransform] = Field(
        description=(
            "Transformation of the element. Applies the passed transform "
            "to the element. Thecontent that does not fit into the "
            "original view will be cut off."
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
    transition_triggers: typing.Optional[typing.List[div_transition_trigger.DivTransitionTrigger]] = Field(
        min_items=1, 
        description=(
            "Animation starting triggers. Default value: `[state_change, "
            "visibility_change]`."
        ),
    )
    visibility: typing.Optional[div_visibility.DivVisibility] = Field(
        description="Element visibility.",
    )
    visibility_action: typing.Optional[div_visibility_action.DivVisibilityAction] = Field(
        description=(
            "Tracking visibility of a single element. Not used if the "
            "`visibility_actions`parameter is set."
        ),
    )
    visibility_actions: typing.Optional[typing.List[div_visibility_action.DivVisibilityAction]] = Field(
        min_items=1, 
        description="Actions when an element appears on the screen.",
    )
    width: typing.Optional[div_size.DivSize] = Field(
        description="Element width.",
    )


class DivGalleryCrossContentAlignment(str, enum.Enum):
    START = "start"
    CENTER = "center"
    END = "end"


class DivGalleryOrientation(str, enum.Enum):
    HORIZONTAL = "horizontal"
    VERTICAL = "vertical"


class DivGalleryScrollMode(str, enum.Enum):
    PAGING = "paging"
    DEFAULT = "default"


DivGallery.update_forward_refs()
