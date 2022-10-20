# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_pivot


# Transformation of the element.
class DivTransform(BaseDiv):

    def __init__(
        self, *,
        pivot_x: typing.Optional[div_pivot.DivPivot] = None,
        pivot_y: typing.Optional[div_pivot.DivPivot] = None,
        rotation: typing.Optional[float] = None,
    ):
        super().__init__(
            pivot_x=pivot_x,
            pivot_y=pivot_y,
            rotation=rotation,
        )

    pivot_x: typing.Optional[div_pivot.DivPivot] = Field(description='The X coordinate of the rotation axis.')
    pivot_y: typing.Optional[div_pivot.DivPivot] = Field(description='The Y coordinate of the rotation axis.')
    rotation: typing.Optional[float] = Field(description='The number of degrees by which the element must be rotated. A positive valuedescribes a clockwise rotation.')


DivTransform.update_forward_refs()
