# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_size_unit


# Stroke.
class DivStroke(BaseDiv):

    def __init__(
        self, *,
        color: typing.Optional[typing.Union[Expr, str]] = None,
        unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = None,
        width: typing.Optional[typing.Union[Expr, int]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            color=color,
            unit=unit,
            width=width,
            **kwargs,
        )

    color: typing.Union[Expr, str] = Field(
        format="color", 
        description="Stroke color.",
    )
    unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = Field(
    )
    width: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Stroke width.",
    )


DivStroke.update_forward_refs()
