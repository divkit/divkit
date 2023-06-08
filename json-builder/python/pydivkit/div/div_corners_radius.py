# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Sets corner rounding.
class DivCornersRadius(BaseDiv):

    def __init__(
        self, *,
        bottom_left: typing.Optional[typing.Union[Expr, int]] = None,
        bottom_right: typing.Optional[typing.Union[Expr, int]] = None,
        top_left: typing.Optional[typing.Union[Expr, int]] = None,
        top_right: typing.Optional[typing.Union[Expr, int]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            bottom_left=bottom_left,
            bottom_right=bottom_right,
            top_left=top_left,
            top_right=top_right,
            **kwargs,
        )

    bottom_left: typing.Optional[typing.Union[Expr, int]] = Field(
        description=(
            "Rounding radius of a lower left corner. If not specified, "
            "then `corner_radius` isused."
        )
        , 
        name="bottom-left",
    )
    bottom_right: typing.Optional[typing.Union[Expr, int]] = Field(
        description=(
            "Rounding radius of a lower right corner. If not specified, "
            "then `corner_radius`is used."
        )
        , 
        name="bottom-right",
    )
    top_left: typing.Optional[typing.Union[Expr, int]] = Field(
        description=(
            "Rounding radius of an upper left corner. If not specified, "
            "then `corner_radius`is used."
        )
        , 
        name="top-left",
    )
    top_right: typing.Optional[typing.Union[Expr, int]] = Field(
        description=(
            "Rounding radius of an upper right corner. If not specified, "
            "then `corner_radius`is used."
        )
        , 
        name="top-right",
    )


DivCornersRadius.update_forward_refs()
