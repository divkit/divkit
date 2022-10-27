# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import div_action, div_background, div_border


# Element behavior when focusing or losing focus.
class DivFocus(BaseDiv):

    def __init__(
        self, *,
        background: typing.Optional[typing.List[div_background.DivBackground]] = None,
        border: typing.Optional[div_border.DivBorder] = None,
        next_focus_ids: typing.Optional[DivFocusNextFocusIds] = None,
        on_blur: typing.Optional[typing.List[div_action.DivAction]] = None,
        on_focus: typing.Optional[typing.List[div_action.DivAction]] = None,
    ):
        super().__init__(
            background=background,
            border=border,
            next_focus_ids=next_focus_ids,
            on_blur=on_blur,
            on_focus=on_focus,
        )

    background: typing.Optional[typing.List[div_background.DivBackground]] = Field(
        min_items=1, 
        description=(
            "Background of an element when it is in focus. It can "
            "contain multiple layers."
        ),
    )
    border: typing.Optional[div_border.DivBorder] = Field(
        description="Border of an element when it is in focus",
    )
    next_focus_ids: typing.Optional[DivFocusNextFocusIds] = Field(
        description="IDs of elements that will be next to get focus.",
    )
    on_blur: typing.Optional[typing.List[div_action.DivAction]] = Field(
        min_items=1, 
        description="Actions when an element loses focus.",
    )
    on_focus: typing.Optional[typing.List[div_action.DivAction]] = Field(
        min_items=1, 
        description="Actions when an element gets focus.",
    )


# IDs of elements that will be next to get focus.
class DivFocusNextFocusIds(BaseDiv):

    def __init__(
        self, *,
        down: typing.Optional[str] = None,
        forward: typing.Optional[str] = None,
        left: typing.Optional[str] = None,
        right: typing.Optional[str] = None,
        up: typing.Optional[str] = None,
    ):
        super().__init__(
            down=down,
            forward=forward,
            left=left,
            right=right,
            up=up,
        )

    down: typing.Optional[str] = Field(
        min_length=1,
    )
    forward: typing.Optional[str] = Field(
        min_length=1,
    )
    left: typing.Optional[str] = Field(
        min_length=1,
    )
    right: typing.Optional[str] = Field(
        min_length=1,
    )
    up: typing.Optional[str] = Field(
        min_length=1,
    )


DivFocusNextFocusIds.update_forward_refs()


DivFocus.update_forward_refs()
