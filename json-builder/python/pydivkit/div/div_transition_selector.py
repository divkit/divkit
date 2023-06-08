# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


class DivTransitionSelector(str, enum.Enum):
    NONE = "none"
    DATA_CHANGE = "data_change"
    STATE_CHANGE = "state_change"
    ANY_CHANGE = "any_change"
