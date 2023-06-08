# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_animation_interpolator


# Scale animation.
class DivScaleTransition(BaseDiv):

    def __init__(
        self, *,
        type: str = "scale",
        duration: typing.Optional[typing.Union[Expr, int]] = None,
        interpolator: typing.Optional[typing.Union[Expr, div_animation_interpolator.DivAnimationInterpolator]] = None,
        pivot_x: typing.Optional[typing.Union[Expr, float]] = None,
        pivot_y: typing.Optional[typing.Union[Expr, float]] = None,
        scale: typing.Optional[typing.Union[Expr, float]] = None,
        start_delay: typing.Optional[typing.Union[Expr, int]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            duration=duration,
            interpolator=interpolator,
            pivot_x=pivot_x,
            pivot_y=pivot_y,
            scale=scale,
            start_delay=start_delay,
            **kwargs,
        )

    type: str = Field(default="scale")
    duration: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Animation duration in milliseconds.",
    )
    interpolator: typing.Optional[typing.Union[Expr, div_animation_interpolator.DivAnimationInterpolator]] = Field(
        description="Transition speed nature.",
    )
    pivot_x: typing.Optional[typing.Union[Expr, float]] = Field(
        description=(
            "Relative coordinate `X` of the point that won\'t change its "
            "position in case ofscaling."
        ),
    )
    pivot_y: typing.Optional[typing.Union[Expr, float]] = Field(
        description=(
            "Relative coordinate `Y` of the point that won\'t change its "
            "position in case ofscaling."
        ),
    )
    scale: typing.Optional[typing.Union[Expr, float]] = Field(
        description=(
            "Value of the scale from which the element starts appearing "
            "or at which itfinishes disappearing."
        ),
    )
    start_delay: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Delay in milliseconds before animation starts.",
    )


DivScaleTransition.update_forward_refs()
