# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field


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

    ratio: float = Field(
        description="`ratio = width / height`.",
    )


DivAspect.update_forward_refs()
