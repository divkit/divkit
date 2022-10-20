# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_point


# Element shadow.
class DivShadow(BaseDiv):

    def __init__(
        self, *,
        offset: div_point.DivPoint,
        alpha: typing.Optional[float] = None,
        blur: typing.Optional[int] = None,
        color: typing.Optional[str] = None,
    ):
        super().__init__(
            alpha=alpha,
            blur=blur,
            color=color,
            offset=offset,
        )

    alpha: typing.Optional[float] = Field(description='Shadow transparency.')
    blur: typing.Optional[int] = Field(description='Blur intensity.')
    color: typing.Optional[str] = Field(format="color", description='Shadow color.')
    offset: div_point.DivPoint = Field(description='Shadow offset.')


DivShadow.update_forward_refs()
