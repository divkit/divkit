# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_action, div_background, div_border


# Element behavior when focusing or losing focus.
class DivFocus(BaseDiv):

    def __init__(
        self, *,
        background: typing.Optional[typing.Sequence[div_background.DivBackground]] = None,
        border: typing.Optional[div_border.DivBorder] = None,
        next_focus_ids: typing.Optional[DivFocusNextFocusIds] = None,
        on_blur: typing.Optional[typing.Sequence[div_action.DivAction]] = None,
        on_focus: typing.Optional[typing.Sequence[div_action.DivAction]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            background=background,
            border=border,
            next_focus_ids=next_focus_ids,
            on_blur=on_blur,
            on_focus=on_focus,
            **kwargs,
        )

    background: typing.Optional[typing.Sequence[div_background.DivBackground]] = Field(
        min_items=1, 
        description=(
            "Background of an element when it is in focus. It can "
            "contain multiple layers."
        ),
    )
    border: typing.Optional[div_border.DivBorder] = Field(
        description="Border of an element when it\'s in focus.",
    )
    next_focus_ids: typing.Optional[DivFocusNextFocusIds] = Field(
        description="IDs of elements that will be next to get focus.",
    )
    on_blur: typing.Optional[typing.Sequence[div_action.DivAction]] = Field(
        min_items=1, 
        description="Actions when an element loses focus.",
    )
    on_focus: typing.Optional[typing.Sequence[div_action.DivAction]] = Field(
        min_items=1, 
        description="Actions when an element gets focus.",
    )


# IDs of elements that will be next to get focus.
class DivFocusNextFocusIds(BaseDiv):

    def __init__(
        self, *,
        down: typing.Optional[typing.Union[Expr, str]] = None,
        forward: typing.Optional[typing.Union[Expr, str]] = None,
        left: typing.Optional[typing.Union[Expr, str]] = None,
        right: typing.Optional[typing.Union[Expr, str]] = None,
        up: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            down=down,
            forward=forward,
            left=left,
            right=right,
            up=up,
            **kwargs,
        )

    down: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1,
    )
    forward: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1,
    )
    left: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1,
    )
    right: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1,
    )
    up: typing.Optional[typing.Union[Expr, str]] = Field(
        min_length=1,
    )


DivFocusNextFocusIds.update_forward_refs()


DivFocus.update_forward_refs()
