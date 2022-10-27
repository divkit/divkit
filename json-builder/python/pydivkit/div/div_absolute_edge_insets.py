# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field


# Sets the margins, without taking the properties of the screen.
class DivAbsoluteEdgeInsets(BaseDiv):

    def __init__(
        self, *,
        bottom: typing.Optional[int] = None,
        left: typing.Optional[int] = None,
        right: typing.Optional[int] = None,
        top: typing.Optional[int] = None,
    ):
        super().__init__(
            bottom=bottom,
            left=left,
            right=right,
            top=top,
        )

    bottom: typing.Optional[int] = Field(
        description="Bottom margin.",
    )
    left: typing.Optional[int] = Field(
        description="Left margin.",
    )
    right: typing.Optional[int] = Field(
        description="Right margin.",
    )
    top: typing.Optional[int] = Field(
        description="Top margin.",
    )


DivAbsoluteEdgeInsets.update_forward_refs()
