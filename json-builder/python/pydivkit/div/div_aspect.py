# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing


# Size with a fixed aspect ratio. It counts height from width and ignores other
# specified height values.
class DivAspect(BaseDiv):

    def __init__(
        self, *,
        ratio: float,
    ):
        super().__init__(
            ratio=ratio,
        )

    ratio: float = Field(description='`ratio = width / height`.')


DivAspect.update_forward_refs()
