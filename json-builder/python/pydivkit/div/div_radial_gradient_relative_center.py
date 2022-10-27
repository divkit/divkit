# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field


# Relative central point of the gradient.
class DivRadialGradientRelativeCenter(BaseDiv):

    def __init__(
        self, *,
        value: float,
        type: str = "relative",
    ):
        super().__init__(
            type=type,
            value=value,
        )

    type: str = Field(default="relative")
    value: float = Field(
        description=(
            "Shift value of the central relative point of the gradient "
            "in the range `0..1`."
        ),
    )


DivRadialGradientRelativeCenter.update_forward_refs()
