# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Sets margins without regard to screen properties.
class DivAbsoluteEdgeInsets(BaseDiv):

    def __init__(
        self, *,
        bottom: typing.Optional[typing.Union[Expr, int]] = None,
        left: typing.Optional[typing.Union[Expr, int]] = None,
        right: typing.Optional[typing.Union[Expr, int]] = None,
        top: typing.Optional[typing.Union[Expr, int]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            bottom=bottom,
            left=left,
            right=right,
            top=top,
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


DivAbsoluteEdgeInsets.update_forward_refs()
