# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

class DivTransitionSelector(str, enum.Enum):
    NONE = 'none'
    DATA_CHANGE = 'data_change'
    STATE_CHANGE = 'state_change'
    ANY_CHANGE = 'any_change'
