# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Solid background color.
class DivSolidBackground(BaseDiv):

    def __init__(
        self, *,
        type: str = "solid",
        color: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            color=color,
            **kwargs,
        )

    type: str = Field(default="solid")
    color: typing.Union[Expr, str] = Field(
        format="color", 
        description="Color.",
    )


DivSolidBackground.update_forward_refs()
