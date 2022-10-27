# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field


# The size of an element adjusts to its contents.
class DivWrapContentSize(BaseDiv):

    def __init__(
        self, *,
        type: str = "wrap_content",
        constrained: typing.Optional[bool] = None,
    ):
        super().__init__(
            type=type,
            constrained=constrained,
        )

    type: str = Field(default="wrap_content")
    constrained: typing.Optional[bool] = Field(
        description=(
            "The final size mustn\'t exceed the parent one. On iOS and "
            "in a default browser`false`. On Android always `true`."
        ),
    )


DivWrapContentSize.update_forward_refs()
