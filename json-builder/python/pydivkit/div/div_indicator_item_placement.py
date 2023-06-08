# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing
from typing import Union

from pydivkit.core import BaseDiv, Expr, Field

from . import (
    div_default_indicator_item_placement, div_stretch_indicator_item_placement,
)


DivIndicatorItemPlacement = Union[
    div_default_indicator_item_placement.DivDefaultIndicatorItemPlacement,
    div_stretch_indicator_item_placement.DivStretchIndicatorItemPlacement,
]
