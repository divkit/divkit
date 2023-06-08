# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing
from typing import Union

from pydivkit.core import BaseDiv, Expr, Field

from . import div_currency_input_mask, div_fixed_length_input_mask


DivInputMask = Union[
    div_fixed_length_input_mask.DivFixedLengthInputMask,
    div_currency_input_mask.DivCurrencyInputMask,
]
