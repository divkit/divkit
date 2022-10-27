# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field


# Location of the coordinate of the axis of rotation as a percentage relative to
# the element.
class DivPivotPercentage(BaseDiv):

    def __init__(
        self, *,
        value: float,
        type: str = "pivot-percentage",
    ):
        super().__init__(
            type=type,
            value=value,
        )

    type: str = Field(default="pivot-percentage")
    value: float = Field(
        description="Location of the element.",
    )


DivPivotPercentage.update_forward_refs()
