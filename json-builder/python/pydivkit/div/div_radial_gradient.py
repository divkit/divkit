# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


# Radial gradient.
class DivRadialGradient(BaseDiv):

    def __init__(
        self, *,
        colors: typing.List[str],
        radius: int,
        type: str = 'radial_gradient',
        center_x: typing.Optional[float] = None,
        center_y: typing.Optional[float] = None,
    ):
        super().__init__(
            type=type,
            center_x=center_x,
            center_y=center_y,
            colors=colors,
            radius=radius,
        )

    type: str = Field(default='radial_gradient')
    center_x: typing.Optional[float] = Field(description='Shift of the central point of the gradient relative to the left edge along the Xaxis in the range `0..1`.')
    center_y: typing.Optional[float] = Field(description='Shift of the central point of the gradient relative to the upper edge along the Yaxis in the range `0..1`.')
    colors: typing.List[str] = Field(min_items=2, description='Colors. Gradient points will be located at an equal distance from each other.')
    radius: int = Field(description='Radius of the gradient transition in `dp`.')


DivRadialGradient.update_forward_refs()
