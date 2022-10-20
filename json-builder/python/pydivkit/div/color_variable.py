# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


# Variable — HEX color as a string.
class ColorVariable(BaseDiv):

    def __init__(
        self, *,
        name: str,
        value: str,
        type: str = 'color',
    ):
        super().__init__(
            type=type,
            name=name,
            value=value,
        )

    type: str = Field(default='color')
    name: str = Field(min_length=1, description='Variable name.')
    value: str = Field(format="color", description='Value.')


ColorVariable.update_forward_refs()
