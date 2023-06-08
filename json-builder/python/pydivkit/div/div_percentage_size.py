# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Element size (%).
class DivPercentageSize(BaseDiv):

    def __init__(
        self, *,
        type: str = "percentage",
        value: typing.Optional[typing.Union[Expr, float]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            value=value,
            **kwargs,
        )

    type: str = Field(default="percentage")
    value: typing.Union[Expr, float] = Field(
        description="Element size value.",
    )


DivPercentageSize.update_forward_refs()
