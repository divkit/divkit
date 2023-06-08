# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Gaussian image blur.
class DivBlur(BaseDiv):

    def __init__(
        self, *,
        type: str = "blur",
        radius: typing.Optional[typing.Union[Expr, int]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            radius=radius,
            **kwargs,
        )

    type: str = Field(default="blur")
    radius: typing.Union[Expr, int] = Field(
        description=(
            "Blur radius. Defines how many pixels blend into each other. "
            "Specified in: `dp`."
        ),
    )


DivBlur.update_forward_refs()
