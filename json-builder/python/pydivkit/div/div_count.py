# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing
from typing import Union

from pydivkit.core import BaseDiv, Expr, Field

from . import div_fixed_count, div_infinity_count


DivCount = Union[
    div_infinity_count.DivInfinityCount,
    div_fixed_count.DivFixedCount,
]
