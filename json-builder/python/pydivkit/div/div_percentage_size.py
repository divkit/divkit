# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field


# Percentage value of the element size.
class DivPercentageSize(BaseDiv):

    def __init__(
        self, *,
        value: float,
        type: str = "percentage",
    ):
        super().__init__(
            type=type,
            value=value,
        )

    type: str = Field(default="percentage")
    value: float = Field(
        description="Element size value.",
    )


DivPercentageSize.update_forward_refs()
