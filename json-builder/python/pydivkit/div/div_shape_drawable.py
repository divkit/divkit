# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import div_shape, div_stroke


# Drawable of a simple geometric shape.
class DivShapeDrawable(BaseDiv):

    def __init__(
        self, *,
        color: str,
        shape: div_shape.DivShape,
        type: str = "shape_drawable",
        stroke: typing.Optional[div_stroke.DivStroke] = None,
    ):
        super().__init__(
            type=type,
            color=color,
            shape=shape,
            stroke=stroke,
        )

    type: str = Field(default="shape_drawable")
    color: str = Field(
        format="color", 
        description="Fill color.",
    )
    shape: div_shape.DivShape = Field(
        description="Shape.",
    )
    stroke: typing.Optional[div_stroke.DivStroke] = Field(
        description="Stroke style.",
    )


DivShapeDrawable.update_forward_refs()
