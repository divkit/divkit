# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_size_unit


# Fixed coordinates of the rotation axis.
class DivPivotFixed(BaseDiv):

    def __init__(
        self, *,
        type: str = "pivot-fixed",
        unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = None,
        value: typing.Optional[typing.Union[Expr, int]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            unit=unit,
            value=value,
            **kwargs,
        )

    type: str = Field(default="pivot-fixed")
    unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = Field(
        description=(
            "Measurement unit. To learn more about units of size "
            "measurement, see [Layoutinside the "
            "card](../../layout.dita)."
        ),
    )
    value: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Coordinate value.",
    )


DivPivotFixed.update_forward_refs()
