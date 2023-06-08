# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing
from typing import Union

from pydivkit.core import BaseDiv, Expr, Field

from . import (
    div_image_background, div_linear_gradient, div_nine_patch_background,
    div_radial_gradient, div_solid_background,
)


DivBackground = Union[
    div_linear_gradient.DivLinearGradient,
    div_radial_gradient.DivRadialGradient,
    div_image_background.DivImageBackground,
    div_solid_background.DivSolidBackground,
    div_nine_patch_background.DivNinePatchBackground,
]
