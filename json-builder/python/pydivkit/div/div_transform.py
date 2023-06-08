# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_pivot


# Transformation of the element.
class DivTransform(BaseDiv):

    def __init__(
        self, *,
        pivot_x: typing.Optional[div_pivot.DivPivot] = None,
        pivot_y: typing.Optional[div_pivot.DivPivot] = None,
        rotation: typing.Optional[typing.Union[Expr, float]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            pivot_x=pivot_x,
            pivot_y=pivot_y,
            rotation=rotation,
            **kwargs,
        )

    pivot_x: typing.Optional[div_pivot.DivPivot] = Field(
        description="X coordinate of the rotation axis.",
    )
    pivot_y: typing.Optional[div_pivot.DivPivot] = Field(
        description="Y coordinate of the rotation axis.",
    )
    rotation: typing.Optional[typing.Union[Expr, float]] = Field(
        description=(
            "Degrees of the element rotation. Positive values used for "
            "clockwise rotation."
        ),
    )


DivTransform.update_forward_refs()
