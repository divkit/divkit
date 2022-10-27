# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import div_size_unit


# Element dimension value.
class DivDimension(BaseDiv):

    def __init__(
        self, *,
        value: float,
        unit: typing.Optional[div_size_unit.DivSizeUnit] = None,
    ):
        super().__init__(
            unit=unit,
            value=value,
        )

    unit: typing.Optional[div_size_unit.DivSizeUnit] = Field(
    )
    value: float = Field(
        description="Value.",
    )


DivDimension.update_forward_refs()
