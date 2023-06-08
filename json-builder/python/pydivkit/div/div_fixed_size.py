# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_size_unit


# Fixed size of an element.
class DivFixedSize(BaseDiv):

    def __init__(
        self, *,
        type: str = "fixed",
        unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = None,
        value: typing.Optional[typing.Union[Expr, int]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            unit=unit,
            value=value,
            **kwargs,
        )

    type: str = Field(default="fixed")
    unit: typing.Optional[typing.Union[Expr, div_size_unit.DivSizeUnit]] = Field(
        description=(
            "Unit of measurement. To learn more about units of size "
            "measurement, see [Layoutinside the "
            "card](../../layout.dita)."
        ),
    )
    value: typing.Union[Expr, int] = Field(
        description="Element size.",
    )


DivFixedSize.update_forward_refs()
