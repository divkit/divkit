# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import div_size_unit


# Fixed central point of the gradient.
class DivRadialGradientFixedCenter(BaseDiv):

    def __init__(
        self, *,
        value: int,
        type: str = "fixed",
        unit: typing.Optional[div_size_unit.DivSizeUnit] = None,
    ):
        super().__init__(
            type=type,
            unit=unit,
            value=value,
        )

    type: str = Field(default="fixed")
    unit: typing.Optional[div_size_unit.DivSizeUnit] = Field(
        description=(
            "Unit of measurement. To learn more about units of size "
            "measurement, see [Layoutinside the "
            "card](../../layout.dita)."
        ),
    )
    value: int = Field(
        description="Shift value of the fixed central point of the gradient.",
    )


DivRadialGradientFixedCenter.update_forward_refs()
