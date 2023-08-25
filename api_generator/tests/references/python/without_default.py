# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing


class WithoutDefault(BaseDiv):

    def __init__(
        self, *,
        type: str = 'non_default',
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            **kwargs,
        )

    type: str = Field(default='non_default')


WithoutDefault.update_forward_refs()
