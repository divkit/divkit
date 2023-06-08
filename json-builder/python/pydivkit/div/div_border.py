# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_corners_radius, div_shadow, div_stroke


# Stroke around the element.
class DivBorder(BaseDiv):

    def __init__(
        self, *,
        corner_radius: typing.Optional[typing.Union[Expr, int]] = None,
        corners_radius: typing.Optional[div_corners_radius.DivCornersRadius] = None,
        has_shadow: typing.Optional[typing.Union[Expr, bool]] = None,
        shadow: typing.Optional[div_shadow.DivShadow] = None,
        stroke: typing.Optional[div_stroke.DivStroke] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            corner_radius=corner_radius,
            corners_radius=corners_radius,
            has_shadow=has_shadow,
            shadow=shadow,
            stroke=stroke,
            **kwargs,
        )

    corner_radius: typing.Optional[typing.Union[Expr, int]] = Field(
        description=(
            "One radius of element and stroke corner rounding. Has a "
            "lower priority than`corners_radius`."
        ),
    )
    corners_radius: typing.Optional[div_corners_radius.DivCornersRadius] = Field(
        description="Multiple radii of element and stroke corner rounding.",
    )
    has_shadow: typing.Optional[typing.Union[Expr, bool]] = Field(
        description="Adding shadow.",
    )
    shadow: typing.Optional[div_shadow.DivShadow] = Field(
        description="Shadow parameters.",
    )
    stroke: typing.Optional[div_stroke.DivStroke] = Field(
        description="Stroke style.",
    )


DivBorder.update_forward_refs()
