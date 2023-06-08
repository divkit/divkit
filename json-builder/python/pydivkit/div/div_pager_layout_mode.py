# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing
from typing import Union

from pydivkit.core import BaseDiv, Expr, Field

from . import div_neighbour_page_size, div_page_size


DivPagerLayoutMode = Union[
    div_page_size.DivPageSize,
    div_neighbour_page_size.DivNeighbourPageSize,
]
