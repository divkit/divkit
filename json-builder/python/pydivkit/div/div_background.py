# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_image_background
from . import div_linear_gradient
from . import div_radial_gradient
from . import div_solid_background

from typing import Union

DivBackground = Union[
    div_linear_gradient.DivLinearGradient,
    div_radial_gradient.DivRadialGradient,
    div_image_background.DivImageBackground,
    div_solid_background.DivSolidBackground,
]
