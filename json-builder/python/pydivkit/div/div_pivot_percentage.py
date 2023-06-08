# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Location of the coordinate of the rotation axis as a percentage relative to the
# element size.
class DivPivotPercentage(BaseDiv):

    def __init__(
        self, *,
        type: str = "pivot-percentage",
        value: typing.Optional[typing.Union[Expr, float]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            value=value,
            **kwargs,
        )

    type: str = Field(default="pivot-percentage")
    value: typing.Union[Expr, float] = Field(
        description="Coordinate value as a percentage.",
    )


DivPivotPercentage.update_forward_refs()
