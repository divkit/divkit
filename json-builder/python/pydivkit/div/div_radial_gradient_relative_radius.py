# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field


# Relative radius of the gradient transition.
class DivRadialGradientRelativeRadius(BaseDiv):

    def __init__(
        self, *,
        value: DivRadialGradientRelativeRadiusValue,
        type: str = "relative",
    ):
        super().__init__(
            type=type,
            value=value,
        )

    type: str = Field(default="relative")
    value: DivRadialGradientRelativeRadiusValue = Field(
        description="Type of relative radius of the gradient transition.",
    )


class DivRadialGradientRelativeRadiusValue(str, enum.Enum):
    NEAREST_CORNER = "nearest_corner"
    FARTHEST_CORNER = "farthest_corner"
    NEAREST_SIDE = "nearest_side"
    FARTHEST_SIDE = "farthest_side"


DivRadialGradientRelativeRadius.update_forward_refs()
