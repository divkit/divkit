# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_animation_interpolator


# Transparency animation.
class DivFadeTransition(BaseDiv):

    def __init__(
        self, *,
        type: str = 'fade',
        alpha: typing.Optional[float] = None,
        duration: typing.Optional[int] = None,
        interpolator: typing.Optional[div_animation_interpolator.DivAnimationInterpolator] = None,
        start_delay: typing.Optional[int] = None,
    ):
        super().__init__(
            type=type,
            alpha=alpha,
            duration=duration,
            interpolator=interpolator,
            start_delay=start_delay,
        )

    type: str = Field(default='fade')
    alpha: typing.Optional[float] = Field(description='Value of the alpha channel which the element starts appearing from or at which itfinishes disappearing.')
    duration: typing.Optional[int] = Field(description='Animation duration in milliseconds.')
    interpolator: typing.Optional[div_animation_interpolator.DivAnimationInterpolator] = Field(description='Transition speed nature.')
    start_delay: typing.Optional[int] = Field(description='Delay in milliseconds before animation starts.')


DivFadeTransition.update_forward_refs()
