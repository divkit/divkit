# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


# A floating-point variable.
class NumberVariable(BaseDiv):

    def __init__(
        self, *,
        name: str,
        value: float,
        type: str = 'number',
    ):
        super().__init__(
            type=type,
            name=name,
            value=value,
        )

    type: str = Field(default='number')
    name: str = Field(min_length=1, description='Variable name.')
    value: float = Field(description='Value.')


NumberVariable.update_forward_refs()
