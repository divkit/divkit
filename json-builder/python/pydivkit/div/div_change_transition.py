# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_change_bounds_transition
from . import div_change_set_transition

from typing import Union

DivChangeTransition = Union[
    div_change_set_transition.DivChangeSetTransition,
    div_change_bounds_transition.DivChangeBoundsTransition,
]
