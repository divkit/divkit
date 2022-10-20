# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


# Extension that affects an element.
class DivExtension(BaseDiv):

    def __init__(
        self, *,
        id: str,
        params: typing.Optional[typing.Dict[str, typing.Any]] = None,
    ):
        super().__init__(
            id=id,
            params=params,
        )

    id: str = Field(min_length=1, description='Extension ID.')
    params: typing.Optional[typing.Dict[str, typing.Any]] = Field(description='Additional extension parameters.')


DivExtension.update_forward_refs()
