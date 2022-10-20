# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_pivot_fixed
from . import div_pivot_percentage

from typing import Union

DivPivot = Union[
    div_pivot_fixed.DivPivotFixed,
    div_pivot_percentage.DivPivotPercentage,
]
