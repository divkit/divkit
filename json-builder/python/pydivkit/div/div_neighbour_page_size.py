# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_fixed_size


# Fixed width value of the visible part of a neighbouring page.
class DivNeighbourPageSize(BaseDiv):

    def __init__(
        self, *,
        type: str = "fixed",
        neighbour_page_width: typing.Optional[div_fixed_size.DivFixedSize] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            neighbour_page_width=neighbour_page_width,
            **kwargs,
        )

    type: str = Field(default="fixed")
    neighbour_page_width: div_fixed_size.DivFixedSize = Field(
        description="Width of the visible part of a neighbouring page.",
    )


DivNeighbourPageSize.update_forward_refs()
