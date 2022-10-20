# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import boolean_variable
from . import color_variable
from . import integer_variable
from . import number_variable
from . import string_variable
from . import url_variable

from typing import Union

DivVariable = Union[
    string_variable.StringVariable,
    number_variable.NumberVariable,
    integer_variable.IntegerVariable,
    boolean_variable.BooleanVariable,
    color_variable.ColorVariable,
    url_variable.UrlVariable,
]
