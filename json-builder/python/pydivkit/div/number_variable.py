# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field


# A floating-point variable.
class NumberVariable(BaseDiv):

    def __init__(
        self, *,
        name: str,
        value: float,
        type: str = "number",
    ):
        super().__init__(
            type=type,
            name=name,
            value=value,
        )

    type: str = Field(default="number")
    name: str = Field(
        min_length=1, 
        description="Variable name.",
    )
    value: float = Field(
        description="Value.",
    )


NumberVariable.update_forward_refs()
