# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


# Fixed number of repetitions.
class DivFixedCount(BaseDiv):

    def __init__(
        self, *,
        value: int,
        type: str = 'fixed',
    ):
        super().__init__(
            type=type,
            value=value,
        )

    type: str = Field(default='fixed')
    value: int = Field(description='Number of repetitions.')


DivFixedCount.update_forward_refs()
