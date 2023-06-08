# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_fixed_size, div_stroke


# Circle.
class DivCircleShape(BaseDiv):

    def __init__(
        self, *,
        type: str = "circle",
        background_color: typing.Optional[typing.Union[Expr, str]] = None,
        radius: typing.Optional[div_fixed_size.DivFixedSize] = None,
        stroke: typing.Optional[div_stroke.DivStroke] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            background_color=background_color,
            radius=radius,
            stroke=stroke,
            **kwargs,
        )

    type: str = Field(default="circle")
    background_color: typing.Optional[typing.Union[Expr, str]] = Field(
        format="color", 
        description="Fill color.",
    )
    radius: typing.Optional[div_fixed_size.DivFixedSize] = Field(
        description="Radius.",
    )
    stroke: typing.Optional[div_stroke.DivStroke] = Field(
        description="Stroke style.",
    )


DivCircleShape.update_forward_refs()
