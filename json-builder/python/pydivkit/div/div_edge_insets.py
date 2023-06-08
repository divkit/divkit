# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_size_unit


# It sets margins.
class DivEdgeInsets(BaseDiv):

    def __init__(
        self, *,
        bottom: typing.Optional[typing.Union[Expr, int]] = None,
        left: typing.Optional[typing.Union[Expr, int]] = None,
        right: typing.Optional[typing.Union[Expr, int]] = None,
        top: typing.Optional[typing.Union[Expr, int]] = None,
        unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            bottom=bottom,
            left=left,
            right=right,
            top=top,
            unit=unit,
            **kwargs,
        )

    bottom: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Bottom margin.",
    )
    left: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Left margin.",
    )
    right: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Right margin.",
    )
    top: typing.Optional[typing.Union[Expr, int]] = Field(
        description="Top margin.",
    )
    unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = Field(
    )


DivEdgeInsets.update_forward_refs()
