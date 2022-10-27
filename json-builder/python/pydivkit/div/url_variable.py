# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field


# Variable — URL as a string.
class UrlVariable(BaseDiv):

    def __init__(
        self, *,
        name: str,
        value: str,
        type: str = "url",
    ):
        super().__init__(
            type=type,
            name=name,
            value=value,
        )

    type: str = Field(default="url")
    name: str = Field(
        min_length=1, 
        description="Variable name.",
    )
    value: str = Field(
        format="uri", 
        description="Value.",
    )


UrlVariable.update_forward_refs()
