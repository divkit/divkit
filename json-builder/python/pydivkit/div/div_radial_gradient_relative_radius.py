# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Relative radius of the gradient transition.
class DivRadialGradientRelativeRadius(BaseDiv):

    def __init__(
        self, *,
        type: str = "relative",
        value: typing.Optional[typing.Union[Expr, DivRadialGradientRelativeRadiusValue]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            value=value,
            **kwargs,
        )

    type: str = Field(default="relative")
    value: typing.Union[Expr, DivRadialGradientRelativeRadiusValue] = Field(
        description="Type of the relative radius of the gradient transition.",
    )


class DivRadialGradientRelativeRadiusValue(str, enum.Enum):
    NEAREST_CORNER = "nearest_corner"
    FARTHEST_CORNER = "farthest_corner"
    NEAREST_SIDE = "nearest_side"
    FARTHEST_SIDE = "farthest_side"


DivRadialGradientRelativeRadius.update_forward_refs()
