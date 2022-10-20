# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


# Variable — URL as a string.
class UrlVariable(BaseDiv):

    def __init__(
        self, *,
        name: str,
        value: str,
        type: str = 'url',
    ):
        super().__init__(
            type=type,
            name=name,
            value=value,
        )

    type: str = Field(default='url')
    name: str = Field(min_length=1, description='Variable name.')
    value: str = Field(format="uri", description='Value.')


UrlVariable.update_forward_refs()
