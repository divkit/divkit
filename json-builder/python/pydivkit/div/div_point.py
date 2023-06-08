# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_dimension


# A point with fixed coordinates.
class DivPoint(BaseDiv):

    def __init__(
        self, *,
        x: typing.Optional[div_dimension.DivDimension] = None,
        y: typing.Optional[div_dimension.DivDimension] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            x=x,
            y=y,
            **kwargs,
        )

    x: div_dimension.DivDimension = Field(
        description="`X` coordinate.",
    )
    y: div_dimension.DivDimension = Field(
        description="`Y` coordinate.",
    )


DivPoint.update_forward_refs()
