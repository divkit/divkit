# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field


# Infinite number of repetitions.
class DivInfinityCount(BaseDiv):

    def __init__(
        self, *,
        type: str = "infinity",
    ):
        super().__init__(
            type=type,
        )

    type: str = Field(default="infinity")


DivInfinityCount.update_forward_refs()
