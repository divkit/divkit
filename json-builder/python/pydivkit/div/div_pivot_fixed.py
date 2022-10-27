# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import div_size_unit


# Value of the offset of the coordinates of the axis of rotation.
class DivPivotFixed(BaseDiv):

    def __init__(
        self, *,
        type: str = "pivot-fixed",
        unit: typing.Optional[div_size_unit.DivSizeUnit] = None,
        value: typing.Optional[int] = None,
    ):
        super().__init__(
            type=type,
            unit=unit,
            value=value,
        )

    type: str = Field(default="pivot-fixed")
    unit: typing.Optional[div_size_unit.DivSizeUnit] = Field(
        description=(
            "Unit of size measurement. To learn more about units of size "
            "measurement, see[Layout inside the "
            "card](../../layout.dita)."
        ),
    )
    value: typing.Optional[int] = Field(
        description="Offset.",
    )


DivPivotFixed.update_forward_refs()
