# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_fixed_size
from . import div_match_parent_size
from . import div_wrap_content_size

from typing import Union

DivSize = Union[
    div_fixed_size.DivFixedSize,
    div_match_parent_size.DivMatchParentSize,
    div_wrap_content_size.DivWrapContentSize,
]
