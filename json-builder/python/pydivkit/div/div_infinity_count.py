# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


# Infinite number of repetitions.
class DivInfinityCount(BaseDiv):

    def __init__(
        self, *,
        type: str = 'infinity',
    ):
        super().__init__(
            type=type,
        )

    type: str = Field(default='infinity')


DivInfinityCount.update_forward_refs()
