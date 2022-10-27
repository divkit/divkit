# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import div_percentage_size


# Percentage value of the page width.
class DivPageSize(BaseDiv):

    def __init__(
        self, *,
        page_width: div_percentage_size.DivPercentageSize,
        type: str = "percentage",
    ):
        super().__init__(
            type=type,
            page_width=page_width,
        )

    type: str = Field(default="percentage")
    page_width: div_percentage_size.DivPercentageSize = Field(
        description="Page width as a percentage of the parent element width.",
    )


DivPageSize.update_forward_refs()
