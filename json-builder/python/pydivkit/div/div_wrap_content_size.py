# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_size_unit


# The size of an element adjusts to its contents.
class DivWrapContentSize(BaseDiv):

    def __init__(
        self, *,
        type: str = "wrap_content",
        constrained: typing.Optional[typing.Union[Expr, bool]] = None,
        max_size: typing.Optional[DivWrapContentSizeConstraintSize] = None,
        min_size: typing.Optional[DivWrapContentSizeConstraintSize] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            constrained=constrained,
            max_size=max_size,
            min_size=min_size,
            **kwargs,
        )

    type: str = Field(default="wrap_content")
    constrained: typing.Optional[typing.Union[Expr, bool]] = Field(
        description=(
            "The final size mustn\'t exceed the parent one. On iOS and "
            "in a default browser`false`. On Android always `true`."
        ),
    )
    max_size: typing.Optional[DivWrapContentSizeConstraintSize] = Field(
        description="Maximum size of an element.",
    )
    min_size: typing.Optional[DivWrapContentSizeConstraintSize] = Field(
        description="Minimum size of an element.",
    )


class DivWrapContentSizeConstraintSize(BaseDiv):

    def __init__(
        self, *,
        unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = None,
        value: typing.Optional[typing.Union[Expr, int]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            unit=unit,
            value=value,
            **kwargs,
        )

    unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = Field(
        description=(
            "Unit of measurement:`px` — a physical pixel.`dp` — a "
            "logical pixel that doesn\'tdepend on screen density.`sp` — "
            "a logical pixel that depends on the font size ona device. "
            "Specify height in `sp`. Only available on Android."
        ),
    )
    value: typing.Union[Expr, int] = Field(
    )


DivWrapContentSizeConstraintSize.update_forward_refs()


DivWrapContentSize.update_forward_refs()
