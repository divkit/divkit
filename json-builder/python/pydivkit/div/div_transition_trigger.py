# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

class DivTransitionTrigger(str, enum.Enum):
    DATA_CHANGE = 'data_change'
    STATE_CHANGE = 'state_change'
    VISIBILITY_CHANGE = 'visibility_change'
