# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import (
    div, div_accessibility, div_action, div_alignment_horizontal,
    div_alignment_vertical, div_appearance_transition, div_background,
    div_border, div_change_transition, div_corners_radius, div_disappear_action,
    div_edge_insets, div_extension, div_focus, div_font_family, div_font_weight,
    div_size, div_size_unit, div_tooltip, div_transform, div_transition_trigger,
    div_visibility, div_visibility_action,
)


# Tabs. Height of the first tab is determined by its contents, and height of the
# remaining [depends on the platform](../../location.dita#tabs).
class DivTabs(BaseDiv):

    def __init__(
        self, *,
        type: str = "tabs",
        accessibility: typing.Optional[div_accessibility.DivAccessibility] = None,
        alignment_horizontal: typing.Optional[typing.Union[Expr, div_alignment_horizontal.DivAlignmentHorizontal]] = None,
        alignment_vertical: typing.Optional[typing.Union[Expr, div_alignment_vertical.DivAlignmentVertical]] = None,
        alpha: typing.Optional[typing.Union[Expr, float]] = None,
        background: typing.Optional[typing.Sequence[div_background.DivBackground]] = None,
        border: typing.Optional[div_border.DivBorder] = None,
        column_span: typing.Optional[typing.Union[Expr, int]] = None,
        disappear_actions: typing.Optional[typing.Sequence[div_disappear_action.DivDisappearAction]] = None,
        dynamic_height: typing.Optional[typing.Union[Expr, bool]] = None,
        extensions: typing.Optional[typing.Sequence[div_extension.DivExtension]] = None,
        focus: typing.Optional[div_focus.DivFocus] = None,
        has_separator: typing.Optional[typing.Union[Expr, bool]] = None,
        height: typing.Optional[div_size.DivSize] = None,
        id: typing.Optional[typing.Union[Expr, str]] = None,
        items: typing.Optional[typing.Sequence[DivTabsItem]] = None,
        margins: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        restrict_parent_scroll: typing.Optional[typing.Union[Expr, bool]] = None,
        row_span: typing.Optional[typing.Union[Expr, int]] = None,
        selected_actions: typing.Optional[typing.Sequence[div_action.DivAction]] = None,
        selected_tab: typing.Optional[typing.Union[Expr, int]] = None,
        separator_color: typing.Optional[typing.Union[Expr, str]] = None,
        separator_paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        switch_tabs_by_content_swipe_enabled: typing.Optional[typing.Union[Expr, bool]] = None,
        tab_title_style: typing.Optional[DivTabsTabTitleStyle] = None,
        title_paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
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
            alignment_horizontal=alignment_horizontal,
            alignment_vertical=alignment_vertical,
            alpha=alpha,
            background=background,
            border=border,
            column_span=column_span,
            disappear_actions=disappear_actions,
            dynamic_height=dynamic_height,
            extensions=extensions,
            focus=focus,
            has_separator=has_separator,
            height=height,
            id=id,
            items=items,
            margins=margins,
            paddings=paddings,
            restrict_parent_scroll=restrict_parent_scroll,
            row_span=row_span,
            selected_actions=selected_actions,
            selected_tab=selected_tab,
            separator_color=separator_color,
            separator_paddings=separator_paddings,
            switch_tabs_by_content_swipe_enabled=switch_tabs_by_content_swipe_enabled,
            tab_title_style=tab_title_style,
            title_paddings=title_paddings,
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

    type: str = Field(default="tabs")
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
    dynamic_height: typing.Optional[typing.Union[Expr, bool]] = Field(
        description=(
            "Updating height when changing the active element. In the "
            "browser, the value isalways `true`."
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
    has_separator: typing.Optional[typing.Union[Expr, bool]] = Field(
        description="A separating line between tabs and contents.",
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
    items: typing.Sequence[DivTabsItem] = Field(
        min_items=1, 
        description=(
            "Tabs. Transition between tabs can be "
            "implementedusing:`div-action://set_current_item?id=&item=` "
            "— set the current tab with anordinal number `item` inside "
            "an element, with the "
            "specified`id`;`div-action://set_next_item?id=[&overflow={cl"
            "amp|ring}]` — go to the nexttab inside an element, with the "
            "specified`id`;`div-action://set_previous_item?id=[&overflow"
            "={clamp|ring}]` — go to theprevious tab inside an element, "
            "with the specified `id`.</p><p>The optional`overflow` "
            "parameter is used to set navigation when the first or last "
            "element isreached:`clamp` — transition will stop at the "
            "border element;`ring` — go to thebeginning or end, "
            "depending on the current element.</p><p>By default, "
            "`clamp`."
        ),
    )
    margins: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(
        description="External margins from the element stroke.",
    )
    paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(
        description="Internal margins from the element stroke.",
    )
    restrict_parent_scroll: typing.Optional[typing.Union[Expr, bool]] = Field(
        description=(
            "If the parameter is enabled, tabs won\'t transmit the "
            "scroll gesture to the parentelement."
        ),
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
    selected_tab: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Ordinal number of the tab that will be opened by default.",
    )
    separator_color: typing.Optional[typing.Union[Expr, str]] = Field(
        format="color", 
        description="Separator color.",
    )
    separator_paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(
        description=(
            "Indents from the separating line. Not used if "
            "`has_separator = false`."
        ),
    )
    switch_tabs_by_content_swipe_enabled: typing.Optional[typing.Union[Expr, bool]] = Field(
        description="Switching tabs by scrolling through the contents.",
    )
    tab_title_style: typing.Optional[DivTabsTabTitleStyle] = Field(
        description="Design style of tab titles.",
    )
    title_paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(
        description="Indents in the tab name.",
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


# Tab.
class DivTabsItem(BaseDiv):

    def __init__(
        self, *,
        div: typing.Optional[div.Div] = None,
        title: typing.Optional[typing.Union[Expr, str]] = None,
        title_click_action: typing.Optional[div_action.DivAction] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            div=div,
            title=title,
            title_click_action=title_click_action,
            **kwargs,
        )

    div: div.Div = Field(
        description="Tab contents.",
    )
    title: typing.Union[Expr, str] = Field(
        min_length=1, 
        description="Tab title.",
    )
    title_click_action: typing.Optional[div_action.DivAction] = Field(
        description="Action when clicking on the active tab title.",
    )


DivTabsItem.update_forward_refs()


# Design style of tab titles.
class DivTabsTabTitleStyle(BaseDiv):

    def __init__(
        self, *,
        active_background_color: typing.Optional[typing.Union[Expr, str]] = None,
        active_font_weight: typing.Optional[typing.Union[Expr, div_font_weight.DivFontWeight]] = None,
        active_text_color: typing.Optional[typing.Union[Expr, str]] = None,
        animation_duration: typing.Optional[typing.Union[Expr, int]] = None,
        animation_type: typing.Optional[typing.Union[Expr, TabTitleStyleAnimationType]] = None,
        corner_radius: typing.Optional[typing.Union[Expr, int]] = None,
        corners_radius: typing.Optional[div_corners_radius.DivCornersRadius] = None,
        font_family: typing.Optional[typing.Union[Expr, div_font_family.DivFontFamily]] = None,
        font_size: typing.Optional[typing.Union[Expr, int]] = None,
        font_size_unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = None,
        font_weight: typing.Optional[typing.Union[Expr, div_font_weight.DivFontWeight]] = None,
        inactive_background_color: typing.Optional[typing.Union[Expr, str]] = None,
        inactive_font_weight: typing.Optional[typing.Union[Expr, div_font_weight.DivFontWeight]] = None,
        inactive_text_color: typing.Optional[typing.Union[Expr, str]] = None,
        item_spacing: typing.Optional[typing.Union[Expr, int]] = None,
        letter_spacing: typing.Optional[typing.Union[Expr, float]] = None,
        line_height: typing.Optional[typing.Union[Expr, int]] = None,
        paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            active_background_color=active_background_color,
            active_font_weight=active_font_weight,
            active_text_color=active_text_color,
            animation_duration=animation_duration,
            animation_type=animation_type,
            corner_radius=corner_radius,
            corners_radius=corners_radius,
            font_family=font_family,
            font_size=font_size,
            font_size_unit=font_size_unit,
            font_weight=font_weight,
            inactive_background_color=inactive_background_color,
            inactive_font_weight=inactive_font_weight,
            inactive_text_color=inactive_text_color,
            item_spacing=item_spacing,
            letter_spacing=letter_spacing,
            line_height=line_height,
            paddings=paddings,
            **kwargs,
        )

    active_background_color: typing.Optional[typing.Union[Expr, str]] = Field(
        format="color", 
        description="Background color of the active tab title.",
    )
    active_font_weight: typing.Optional[typing.Union[Expr, div_font_weight.DivFontWeight]] = Field(
        description="Active tab title style.",
    )
    active_text_color: typing.Optional[typing.Union[Expr, str]] = Field(
        format="color", 
        description="Color of the active tab title text.",
    )
    animation_duration: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Duration of active title change animation.",
    )
    animation_type: typing.Optional[typing.Union[Expr, TabTitleStyleAnimationType]] = Field(
        description="Active title change animation.",
    )
    corner_radius: typing.Optional[typing.Union[Expr, int]] = Field(
        description=(
            "Title corner rounding radius. If the parameter isn\'t "
            "specified, the rounding ismaximum (half of the smallest "
            "size). Not used if the `corners_radius` parameteris set."
        ),
    )
    corners_radius: typing.Optional[div_corners_radius.DivCornersRadius] = Field(
        description=(
            "Rounding radii of corners of multiple titles. Empty values "
            "are replaced by`corner_radius`."
        ),
    )
    font_family: typing.Optional[typing.Union[Expr, div_font_family.DivFontFamily]] = Field(
        description=(
            "Font family:`text` — a standard text font;`display` — a "
            "family of fonts with alarge font size."
        ),
    )
    font_size: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Title font size.",
    )
    font_size_unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = Field(
        description="Units of title font size measurement.",
    )
    font_weight: typing.Optional[typing.Union[Expr, div_font_weight.DivFontWeight]] = Field(
        description=(
            "Style. Use `active_font_weight` and `inactive_font_weight` "
            "instead. @deprecated"
        ),
    )
    inactive_background_color: typing.Optional[typing.Union[Expr, str]] = Field(
        format="color", 
        description="Background color of the inactive tab title.",
    )
    inactive_font_weight: typing.Optional[typing.Union[Expr, div_font_weight.DivFontWeight]] = Field(
        description="Inactive tab title style.",
    )
    inactive_text_color: typing.Optional[typing.Union[Expr, str]] = Field(
        format="color", 
        description="Color of the inactive tab title text.",
    )
    item_spacing: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Spacing between neighbouring tab titles.",
    )
    letter_spacing: typing.Optional[typing.Union[Expr, float]] = Field(
        description="Spacing between title characters.",
    )
    line_height: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Line spacing of the text.",
    )
    paddings: typing.Optional[div_edge_insets.DivEdgeInsets] = Field(
        description="Indents around the tab title.",
    )


class TabTitleStyleAnimationType(str, enum.Enum):
    SLIDE = "slide"
    FADE = "fade"
    NONE = "none"


DivTabsTabTitleStyle.update_forward_refs()


DivTabs.update_forward_refs()
