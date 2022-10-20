# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_size_unit


# Stroke.
class DivStroke(BaseDiv):

    def __init__(
        self, *,
        color: str,
        unit: typing.Optional[div_size_unit.DivSizeUnit] = None,
        width: typing.Optional[int] = None,
    ):
        super().__init__(
            color=color,
            unit=unit,
            width=width,
        )

    color: str = Field(format="color", description='Stroke color.')
    unit: typing.Optional[div_size_unit.DivSizeUnit] = Field()
    width: typing.Optional[int] = Field(description='Stroke width.')


DivStroke.update_forward_refs()
