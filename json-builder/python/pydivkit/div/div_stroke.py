# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import div_size_unit


# Stroke.
class DivStroke(BaseDiv):

    def __init__(
        self, *,
        color: str,
        unit: typing.Optional[div_size_unit.DivSizeUnit] = None,
        width: typing.Optional[int] = None,
    ):
        super().__init__(
            color=color,
            unit=unit,
            width=width,
        )

    color: str = Field(
        format="color", 
        description="Stroke color.",
    )
    unit: typing.Optional[div_size_unit.DivSizeUnit] = Field(
    )
    width: typing.Optional[int] = Field(
        description="Stroke width.",
    )


DivStroke.update_forward_refs()
