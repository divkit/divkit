# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Linear gradient.
class DivLinearGradient(BaseDiv):

    def __init__(
        self, *,
        type: str = "gradient",
        angle: typing.Optional[typing.Union[Expr, int]] = None,
        colors: typing.Optional[typing.Sequence[typing.Union[Expr, str]]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            angle=angle,
            colors=colors,
            **kwargs,
        )

    type: str = Field(default="gradient")
    angle: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Angle of gradient direction.",
    )
    colors: typing.Sequence[typing.Union[Expr, str]] = Field(
        min_items=2, 
        description=(
            "Colors. Gradient points are located at an equal distance "
            "from each other."
        ),
    )


DivLinearGradient.update_forward_refs()
