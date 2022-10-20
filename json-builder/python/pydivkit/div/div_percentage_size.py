# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


# Percentage value of the element size.
class DivPercentageSize(BaseDiv):

    def __init__(
        self, *,
        value: float,
        type: str = 'percentage',
    ):
        super().__init__(
            type=type,
            value=value,
        )

    type: str = Field(default='percentage')
    value: float = Field(description='Element size value.')


DivPercentageSize.update_forward_refs()
