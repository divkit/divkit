# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_size_unit


# Element dimension value.
class DivDimension(BaseDiv):

    def __init__(
        self, *,
        unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = None,
        value: typing.Optional[typing.Union[Expr, float]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            unit=unit,
            value=value,
            **kwargs,
        )

    unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = Field(
    )
    value: typing.Union[Expr, float] = Field(
        description="Value.",
    )


DivDimension.update_forward_refs()
