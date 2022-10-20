# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


# Location of the coordinate of the axis of rotation as a percentage relative to
# the element.
class DivPivotPercentage(BaseDiv):

    def __init__(
        self, *,
        value: float,
        type: str = 'pivot-percentage',
    ):
        super().__init__(
            type=type,
            value=value,
        )

    type: str = Field(default='pivot-percentage')
    value: float = Field(description='Location of the element.')


DivPivotPercentage.update_forward_refs()
