# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_percentage_size


# Percentage value of the page width.
class DivPageSize(BaseDiv):

    def __init__(
        self, *,
        page_width: div_percentage_size.DivPercentageSize,
        type: str = 'percentage',
    ):
        super().__init__(
            type=type,
            page_width=page_width,
        )

    type: str = Field(default='percentage')
    page_width: div_percentage_size.DivPercentageSize = Field(description='Page width as a percentage of the parent element width.')


DivPageSize.update_forward_refs()
