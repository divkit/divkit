# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_stroke


# Character range border.
class DivTextRangeBorder(BaseDiv):

    def __init__(
        self, *,
        corner_radius: typing.Optional[typing.Union[Expr, int]] = None,
        stroke: typing.Optional[div_stroke.DivStroke] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            corner_radius=corner_radius,
            stroke=stroke,
            **kwargs,
        )

    corner_radius: typing.Optional[typing.Union[Expr, int]] = Field(
        description=(
            "One radius of element and stroke corner rounding. Has a "
            "lower priority than`corners_radius`."
        ),
    )
    stroke: typing.Optional[div_stroke.DivStroke] = Field(
        description="Stroke style.",
    )


DivTextRangeBorder.update_forward_refs()
