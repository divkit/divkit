# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing
from typing import Union

from pydivkit.core import BaseDiv, Expr, Field

from . import div_pivot_fixed, div_pivot_percentage


DivPivot = Union[
    div_pivot_fixed.DivPivotFixed,
    div_pivot_percentage.DivPivotPercentage,
]
