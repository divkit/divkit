# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field


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

    id: str = Field(
        min_length=1, 
        description="Extension ID.",
    )
    params: typing.Optional[typing.Dict[str, typing.Any]] = Field(
        description="Additional extension parameters.",
    )


DivExtension.update_forward_refs()
