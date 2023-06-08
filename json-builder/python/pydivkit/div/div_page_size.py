# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_percentage_size


# Page width (%).
class DivPageSize(BaseDiv):

    def __init__(
        self, *,
        type: str = "percentage",
        page_width: typing.Optional[div_percentage_size.DivPercentageSize] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            page_width=page_width,
            **kwargs,
        )

    type: str = Field(default="percentage")
    page_width: div_percentage_size.DivPercentageSize = Field(
        description="Page width as a percentage of the parent element width.",
    )


DivPageSize.update_forward_refs()
