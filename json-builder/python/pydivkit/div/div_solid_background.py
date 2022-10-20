# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


# Solid background color.
class DivSolidBackground(BaseDiv):

    def __init__(
        self, *,
        color: str,
        type: str = 'solid',
    ):
        super().__init__(
            type=type,
            color=color,
        )

    type: str = Field(default='solid')
    color: str = Field(format="color", description='Color.')


DivSolidBackground.update_forward_refs()
