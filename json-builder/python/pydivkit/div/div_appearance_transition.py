# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing
from typing import Union

from pydivkit.core import BaseDiv, Expr, Field

from . import (
    div_appearance_set_transition, div_fade_transition, div_scale_transition,
    div_slide_transition,
)


DivAppearanceTransition = Union[
    div_appearance_set_transition.DivAppearanceSetTransition,
    div_fade_transition.DivFadeTransition,
    div_scale_transition.DivScaleTransition,
    div_slide_transition.DivSlideTransition,
]
