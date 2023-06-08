# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_point


# Element shadow.
class DivShadow(BaseDiv):

    def __init__(
        self, *,
        alpha: typing.Optional[typing.Union[Expr, float]] = None,
        blur: typing.Optional[typing.Union[Expr, int]] = None,
        color: typing.Optional[typing.Union[Expr, str]] = None,
        offset: typing.Optional[div_point.DivPoint] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            alpha=alpha,
            blur=blur,
            color=color,
            offset=offset,
            **kwargs,
        )

    alpha: typing.Optional[typing.Union[Expr, float]] = Field(
        description="Shadow transparency.",
    )
    blur: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Blur intensity.",
    )
    color: typing.Optional[typing.Union[Expr, str]] = Field(
        format="color", 
        description="Shadow color.",
    )
    offset: div_point.DivPoint = Field(
        description="Shadow offset.",
    )


DivShadow.update_forward_refs()
