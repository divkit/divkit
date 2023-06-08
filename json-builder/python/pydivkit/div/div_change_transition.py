# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing
from typing import Union

from pydivkit.core import BaseDiv, Expr, Field

from . import div_change_bounds_transition, div_change_set_transition


DivChangeTransition = Union[
    div_change_set_transition.DivChangeSetTransition,
    div_change_bounds_transition.DivChangeBoundsTransition,
]
