# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing
from typing import Union

from pydivkit.core import BaseDiv, Expr, Field

from . import div_fixed_size, div_match_parent_size, div_wrap_content_size


DivSize = Union[
    div_fixed_size.DivFixedSize,
    div_match_parent_size.DivMatchParentSize,
    div_wrap_content_size.DivWrapContentSize,
]
