# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


# Fixed aspect ratio. The element's height is calculated based on the width,
# ignoring the `height` value.
class DivAspect(BaseDiv):

    def __init__(
        self, *,
        ratio: typing.Optional[typing.Union[Expr, float]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            ratio=ratio,
            **kwargs,
        )

    ratio: typing.Union[Expr, float] = Field(
        description="`height = width / ratio`.",
    )


DivAspect.update_forward_refs()
