# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_fixed_size


# Element size adjusts to a parent element.
class DivDefaultIndicatorItemPlacement(BaseDiv):

    def __init__(
        self, *,
        type: str = "default",
        space_between_centers: typing.Optional[div_fixed_size.DivFixedSize] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            space_between_centers=space_between_centers,
            **kwargs,
        )

    type: str = Field(default="default")
    space_between_centers: typing.Optional[div_fixed_size.DivFixedSize] = Field(
        description="Spacing between indicator centers.",
    )


DivDefaultIndicatorItemPlacement.update_forward_refs()
