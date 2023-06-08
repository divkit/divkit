# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_shape, div_stroke


# Drawable of a simple geometric shape.
class DivShapeDrawable(BaseDiv):

    def __init__(
        self, *,
        type: str = "shape_drawable",
        color: typing.Optional[typing.Union[Expr, str]] = None,
        shape: typing.Optional[div_shape.DivShape] = None,
        stroke: typing.Optional[div_stroke.DivStroke] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            color=color,
            shape=shape,
            stroke=stroke,
            **kwargs,
        )

    type: str = Field(default="shape_drawable")
    color: typing.Union[Expr, str] = Field(
        format="color", 
        description="Fill color. @deprecated",
    )
    shape: div_shape.DivShape = Field(
        description="Shape.",
    )
    stroke: typing.Optional[div_stroke.DivStroke] = Field(
        description="Stroke style. @deprecated",
    )


DivShapeDrawable.update_forward_refs()
