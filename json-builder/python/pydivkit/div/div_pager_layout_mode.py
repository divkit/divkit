# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_neighbour_page_size
from . import div_page_size

from typing import Union

DivPagerLayoutMode = Union[
    div_page_size.DivPageSize,
    div_neighbour_page_size.DivNeighbourPageSize,
]
