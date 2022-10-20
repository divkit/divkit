# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_fixed_count
from . import div_infinity_count

from typing import Union

DivCount = Union[
    div_infinity_count.DivInfinityCount,
    div_fixed_count.DivFixedCount,
]
