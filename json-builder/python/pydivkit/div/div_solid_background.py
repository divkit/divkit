# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field


# Solid background color.
class DivSolidBackground(BaseDiv):

    def __init__(
        self, *,
        color: str,
        type: str = "solid",
    ):
        super().__init__(
            type=type,
            color=color,
        )

    type: str = Field(default="solid")
    color: str = Field(
        format="color", 
        description="Color.",
    )


DivSolidBackground.update_forward_refs()
