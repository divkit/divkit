# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


# An integer variable.
class IntegerVariable(BaseDiv):

    def __init__(
        self, *,
        name: str,
        value: int,
        type: str = 'integer',
    ):
        super().__init__(
            type=type,
            name=name,
            value=value,
        )

    type: str = Field(default='integer')
    name: str = Field(min_length=1, description='Variable name.')
    value: int = Field(description='Value.')


IntegerVariable.update_forward_refs()
