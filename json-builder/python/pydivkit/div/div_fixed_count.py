# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field


# Fixed number of repetitions.
class DivFixedCount(BaseDiv):

    def __init__(
        self, *,
        value: int,
        type: str = "fixed",
    ):
        super().__init__(
            type=type,
            value=value,
        )

    type: str = Field(default="fixed")
    value: int = Field(
        description="Number of repetitions.",
    )


DivFixedCount.update_forward_refs()
