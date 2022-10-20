# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


# Linear gradient.
class DivLinearGradient(BaseDiv):

    def __init__(
        self, *,
        colors: typing.List[str],
        type: str = 'gradient',
        angle: typing.Optional[int] = None,
    ):
        super().__init__(
            type=type,
            angle=angle,
            colors=colors,
        )

    type: str = Field(default='gradient')
    angle: typing.Optional[int] = Field(description='Angle of gradient direction.')
    colors: typing.List[str] = Field(min_items=2, description='Colors. Gradient points will be located at an equal distance from each other.')


DivLinearGradient.update_forward_refs()
