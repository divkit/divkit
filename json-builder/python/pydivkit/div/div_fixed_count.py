# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Fixed number of repetitions.
class DivFixedCount(BaseDiv):

    def __init__(
        self, *,
        type: str = "fixed",
        value: typing.Optional[typing.Union[Expr, int]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            value=value,
            **kwargs,
        )

    type: str = Field(default="fixed")
    value: typing.Union[Expr, int] = Field(
        description="Number of repetitions.",
    )


DivFixedCount.update_forward_refs()
