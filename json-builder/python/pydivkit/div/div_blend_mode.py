# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


class DivBlendMode(str, enum.Enum):
    SOURCE_IN = "source_in"
    SOURCE_ATOP = "source_atop"
    DARKEN = "darken"
    LIGHTEN = "lighten"
    MULTIPLY = "multiply"
    SCREEN = "screen"
