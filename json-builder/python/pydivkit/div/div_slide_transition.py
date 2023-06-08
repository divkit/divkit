# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_animation_interpolator, div_dimension


# Slide animation.
class DivSlideTransition(BaseDiv):

    def __init__(
        self, *,
        type: str = "slide",
        distance: typing.Optional[div_dimension.DivDimension] = None,
        duration: typing.Optional[typing.Union[Expr, int]] = None,
        edge: typing.Optional[typing.Union[Expr, DivSlideTransitionEdge]] = None,
        interpolator: typing.Optional[typing.Union[Expr, div_animation_interpolator.DivAnimationInterpolator]] = None,
        start_delay: typing.Optional[typing.Union[Expr, int]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            distance=distance,
            duration=duration,
            edge=edge,
            interpolator=interpolator,
            start_delay=start_delay,
            **kwargs,
        )

    type: str = Field(default="slide")
    distance: typing.Optional[div_dimension.DivDimension] = Field(
        description=(
            "A fixed value of an offset which the element starts "
            "appearing from or at which itfinishes disappearing. If no "
            "value is specified, the distance to the selectededge of a "
            "parent element is used."
        ),
    )
    duration: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Animation duration in milliseconds.",
    )
    edge: typing.Optional[typing.Union[Expr, DivSlideTransitionEdge]] = Field(
        description=(
            "Edge of a parent element for one of the action types:where "
            "the element will movefrom when appearing;where the element "
            "will move to when disappearing."
        ),
    )
    interpolator: typing.Optional[typing.Union[Expr, div_animation_interpolator.DivAnimationInterpolator]] = Field(
        description="Transition speed nature.",
    )
    start_delay: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Delay in milliseconds before animation starts.",
    )


class DivSlideTransitionEdge(str, enum.Enum):
    LEFT = "left"
    TOP = "top"
    RIGHT = "right"
    BOTTOM = "bottom"


DivSlideTransition.update_forward_refs()
