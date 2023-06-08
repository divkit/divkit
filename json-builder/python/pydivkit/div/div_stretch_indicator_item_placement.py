# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_fixed_size


# Element size adjusts to a parent element.
class DivStretchIndicatorItemPlacement(BaseDiv):

    def __init__(
        self, *,
        type: str = "stretch",
        item_spacing: typing.Optional[div_fixed_size.DivFixedSize] = None,
        max_visible_items: typing.Optional[typing.Union[Expr, int]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            item_spacing=item_spacing,
            max_visible_items=max_visible_items,
            **kwargs,
        )

    type: str = Field(default="stretch")
    item_spacing: typing.Optional[div_fixed_size.DivFixedSize] = Field(
        description="Spacing between indicator centers.",
    )
    max_visible_items: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Maximum number of visible indicators.",
    )


DivStretchIndicatorItemPlacement.update_forward_refs()
