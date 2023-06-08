# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


class DivSizeUnit(str, enum.Enum):
    DP = "dp"
    SP = "sp"
    PX = "px"
