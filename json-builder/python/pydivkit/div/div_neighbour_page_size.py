# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import div_fixed_size


# Fixed width value of the visible part of a neighbouring page.
class DivNeighbourPageSize(BaseDiv):

    def __init__(
        self, *,
        neighbour_page_width: div_fixed_size.DivFixedSize,
        type: str = "fixed",
    ):
        super().__init__(
            type=type,
            neighbour_page_width=neighbour_page_width,
        )

    type: str = Field(default="fixed")
    neighbour_page_width: div_fixed_size.DivFixedSize = Field(
        description="Width of the visible part of a neighbouring page.",
    )


DivNeighbourPageSize.update_forward_refs()
