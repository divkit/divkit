# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


class DivTransitionTrigger(str, enum.Enum):
    DATA_CHANGE = "data_change"
    STATE_CHANGE = "state_change"
    VISIBILITY_CHANGE = "visibility_change"
