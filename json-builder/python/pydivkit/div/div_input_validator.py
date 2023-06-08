# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing
from typing import Union

from pydivkit.core import BaseDiv, Expr, Field

from . import div_input_validator_expression, div_input_validator_regex


DivInputValidator = Union[
    div_input_validator_regex.DivInputValidatorRegex,
    div_input_validator_expression.DivInputValidatorExpression,
]
