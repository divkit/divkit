# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import div_fixed_size


# A rectangle with rounded corners.
class DivRoundedRectangleShape(BaseDiv):

    def __init__(
        self, *,
        type: str = "rounded_rectangle",
        corner_radius: typing.Optional[div_fixed_size.DivFixedSize] = None,
        item_height: typing.Optional[div_fixed_size.DivFixedSize] = None,
        item_width: typing.Optional[div_fixed_size.DivFixedSize] = None,
    ):
        super().__init__(
            type=type,
            corner_radius=corner_radius,
            item_height=item_height,
            item_width=item_width,
        )

    type: str = Field(default="rounded_rectangle")
    corner_radius: typing.Optional[div_fixed_size.DivFixedSize] = Field(
        description="Corner rounding radius.",
    )
    item_height: typing.Optional[div_fixed_size.DivFixedSize] = Field(
        description="Height.",
    )
    item_width: typing.Optional[div_fixed_size.DivFixedSize] = Field(
        description="Width.",
    )


DivRoundedRectangleShape.update_forward_refs()
