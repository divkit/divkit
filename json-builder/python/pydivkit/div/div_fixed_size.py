# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_size_unit


# Fixed size of an element.
class DivFixedSize(BaseDiv):

    def __init__(
        self, *,
        value: int,
        type: str = 'fixed',
        unit: typing.Optional[div_size_unit.DivSizeUnit] = None,
    ):
        super().__init__(
            type=type,
            unit=unit,
            value=value,
        )

    type: str = Field(default='fixed')
    unit: typing.Optional[div_size_unit.DivSizeUnit] = Field(description='Unit of measurement. To learn more about units of size measurement, see [Layoutinside the card](../../layout.dita).')
    value: int = Field(description='Element size.')


DivFixedSize.update_forward_refs()
