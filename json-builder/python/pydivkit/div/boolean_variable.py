# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field


# A Boolean variable in binary format.
class BooleanVariable(BaseDiv):

    def __init__(
        self, *,
        name: str,
        value: bool,
        type: str = "boolean",
    ):
        super().__init__(
            type=type,
            name=name,
            value=value,
        )

    type: str = Field(default="boolean")
    name: str = Field(
        min_length=1, 
        description="Variable name.",
    )
    value: bool = Field(
        description="Value.",
    )


BooleanVariable.update_forward_refs()
