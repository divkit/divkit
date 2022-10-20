# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


# A string variable.
class StringVariable(BaseDiv):

    def __init__(
        self, *,
        name: str,
        value: str,
        type: str = 'string',
    ):
        super().__init__(
            type=type,
            name=name,
            value=value,
        )

    type: str = Field(default='string')
    name: str = Field(min_length=1, description='Variable name.')
    value: str = Field(description='Value.')


StringVariable.update_forward_refs()
