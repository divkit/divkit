# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field


# An integer variable.
class IntegerVariable(BaseDiv):

    def __init__(
        self, *,
        name: str,
        value: int,
        type: str = "integer",
    ):
        super().__init__(
            type=type,
            name=name,
            value=value,
        )

    type: str = Field(default="integer")
    name: str = Field(
        min_length=1, 
        description="Variable name.",
    )
    value: int = Field(
        description="Value.",
    )


IntegerVariable.update_forward_refs()
