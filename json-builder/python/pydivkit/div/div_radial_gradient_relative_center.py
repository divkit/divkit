# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Location of the central point of the gradient relative to the element borders.
class DivRadialGradientRelativeCenter(BaseDiv):

    def __init__(
        self, *,
        type: str = "relative",
        value: typing.Optional[typing.Union[Expr, float]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            value=value,
            **kwargs,
        )

    type: str = Field(default="relative")
    value: typing.Union[Expr, float] = Field(
        description="Coordinate value in the range \"0...1\".",
    )


DivRadialGradientRelativeCenter.update_forward_refs()
