# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import div_fixed_size


# A circle.
class DivCircleShape(BaseDiv):

    def __init__(
        self, *,
        type: str = "circle",
        radius: typing.Optional[div_fixed_size.DivFixedSize] = None,
    ):
        super().__init__(
            type=type,
            radius=radius,
        )

    type: str = Field(default="circle")
    radius: typing.Optional[div_fixed_size.DivFixedSize] = Field(
        description="Radius.",
    )


DivCircleShape.update_forward_refs()
