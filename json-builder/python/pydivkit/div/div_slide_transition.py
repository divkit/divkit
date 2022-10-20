# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_animation_interpolator
from . import div_dimension


# Slide animation.
class DivSlideTransition(BaseDiv):

    def __init__(
        self, *,
        type: str = 'slide',
        distance: typing.Optional[div_dimension.DivDimension] = None,
        duration: typing.Optional[int] = None,
        edge: typing.Optional[DivSlideTransitionEdge] = None,
        interpolator: typing.Optional[div_animation_interpolator.DivAnimationInterpolator] = None,
        start_delay: typing.Optional[int] = None,
    ):
        super().__init__(
            type=type,
            distance=distance,
            duration=duration,
            edge=edge,
            interpolator=interpolator,
            start_delay=start_delay,
        )

    type: str = Field(default='slide')
    distance: typing.Optional[div_dimension.DivDimension] = Field(description='A fixed value of an offset which the element starts appearing from or at which itfinishes disappearing. If no value is specified, the distance to the selectededge of a parent element is used.')
    duration: typing.Optional[int] = Field(description='Animation duration in milliseconds.')
    edge: typing.Optional[DivSlideTransitionEdge] = Field(description='Edge of a parent element for one of the action types:where the element will movefrom when appearing;where the element will move to when disappearing.')
    interpolator: typing.Optional[div_animation_interpolator.DivAnimationInterpolator] = Field(description='Transition speed nature.')
    start_delay: typing.Optional[int] = Field(description='Delay in milliseconds before animation starts.')


class DivSlideTransitionEdge(str, enum.Enum):
    LEFT = 'left'
    TOP = 'top'
    RIGHT = 'right'
    BOTTOM = 'bottom'


DivSlideTransition.update_forward_refs()
