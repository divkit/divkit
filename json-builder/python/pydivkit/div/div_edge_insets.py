# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_size_unit


# It sets margins.
class DivEdgeInsets(BaseDiv):

    def __init__(
        self, *,
        bottom: typing.Optional[int] = None,
        left: typing.Optional[int] = None,
        right: typing.Optional[int] = None,
        top: typing.Optional[int] = None,
        unit: typing.Optional[div_size_unit.DivSizeUnit] = None,
    ):
        super().__init__(
            bottom=bottom,
            left=left,
            right=right,
            top=top,
            unit=unit,
        )

    bottom: typing.Optional[int] = Field(description='Bottom margin.')
    left: typing.Optional[int] = Field(description='Left margin.')
    right: typing.Optional[int] = Field(description='Right margin.')
    top: typing.Optional[int] = Field(description='Top margin.')
    unit: typing.Optional[div_size_unit.DivSizeUnit] = Field()


DivEdgeInsets.update_forward_refs()
