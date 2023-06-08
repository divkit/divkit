# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_fixed_size, div_stroke


# A rectangle with rounded corners.
class DivRoundedRectangleShape(BaseDiv):

    def __init__(
        self, *,
        type: str = "rounded_rectangle",
        background_color: typing.Optional[typing.Union[Expr, str]] = None,
        corner_radius: typing.Optional[div_fixed_size.DivFixedSize] = None,
        item_height: typing.Optional[div_fixed_size.DivFixedSize] = None,
        item_width: typing.Optional[div_fixed_size.DivFixedSize] = None,
        stroke: typing.Optional[div_stroke.DivStroke] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            background_color=background_color,
            corner_radius=corner_radius,
            item_height=item_height,
            item_width=item_width,
            stroke=stroke,
            **kwargs,
        )

    type: str = Field(default="rounded_rectangle")
    background_color: typing.Optional[typing.Union[Expr, str]] = Field(
        format="color", 
        description="Fill color.",
    )
    corner_radius: typing.Optional[div_fixed_size.DivFixedSize] = Field(
        description="Corner rounding radius.",
    )
    item_height: typing.Optional[div_fixed_size.DivFixedSize] = Field(
        description="Height.",
    )
    item_width: typing.Optional[div_fixed_size.DivFixedSize] = Field(
        description="Width.",
    )
    stroke: typing.Optional[div_stroke.DivStroke] = Field(
        description="Stroke style.",
    )


DivRoundedRectangleShape.update_forward_refs()
