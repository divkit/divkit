# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing
from typing import Union

from pydivkit.core import BaseDiv, Field

from . import (
    boolean_variable, color_variable, integer_variable, number_variable,
    string_variable, url_variable,
)


DivVariable = Union[
    string_variable.StringVariable,
    number_variable.NumberVariable,
    integer_variable.IntegerVariable,
    boolean_variable.BooleanVariable,
    color_variable.ColorVariable,
    url_variable.UrlVariable,
]
