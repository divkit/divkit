# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import div_radial_gradient_center, div_radial_gradient_radius


# Radial gradient.
class DivRadialGradient(BaseDiv):

    def __init__(
        self, *,
        colors: typing.List[str],
        type: str = "radial_gradient",
        center_x: typing.Optional[div_radial_gradient_center.DivRadialGradientCenter] = None,
        center_y: typing.Optional[div_radial_gradient_center.DivRadialGradientCenter] = None,
        radius: typing.Optional[div_radial_gradient_radius.DivRadialGradientRadius] = None,
    ):
        super().__init__(
            type=type,
            center_x=center_x,
            center_y=center_y,
            colors=colors,
            radius=radius,
        )

    type: str = Field(default="radial_gradient")
    center_x: typing.Optional[div_radial_gradient_center.DivRadialGradientCenter] = Field(
        description=(
            "Shift of the central point of the gradient relative to the "
            "left edge along the Xaxis."
        ),
    )
    center_y: typing.Optional[div_radial_gradient_center.DivRadialGradientCenter] = Field(
        description=(
            "Shift of the central point of the gradient relative to the "
            "upper edge along the Yaxis."
        ),
    )
    colors: typing.List[str] = Field(
        min_items=2, 
        description=(
            "Colors. Gradient points will be located at an equal "
            "distance from each other."
        ),
    )
    radius: typing.Optional[div_radial_gradient_radius.DivRadialGradientRadius] = Field(
        description="Radius of the gradient transition.",
    )


DivRadialGradient.update_forward_refs()
