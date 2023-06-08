# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing
from typing import Union

from pydivkit.core import BaseDiv, Expr, Field

from . import div_linear_gradient, div_radial_gradient


DivTextGradient = Union[
    div_linear_gradient.DivLinearGradient,
    div_radial_gradient.DivRadialGradient,
]
