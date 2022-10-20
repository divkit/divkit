# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_appearance_set_transition
from . import div_fade_transition
from . import div_scale_transition
from . import div_slide_transition

from typing import Union

DivAppearanceTransition = Union[
    div_appearance_set_transition.DivAppearanceSetTransition,
    div_fade_transition.DivFadeTransition,
    div_scale_transition.DivScaleTransition,
    div_slide_transition.DivSlideTransition,
]
